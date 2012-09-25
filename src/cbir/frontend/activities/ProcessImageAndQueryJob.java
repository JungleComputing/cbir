package cbir.frontend.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import ibis.constellation.context.OrActivityContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.MatchTable;
import cbir.backend.MultiArchiveIndex;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.events.QueryResultEvent;
import cbir.kernels.activities.CalculateEndmembers;
import cbir.metadata.Metadata;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

public class ProcessImageAndQueryJob extends Activity {

    private static final Logger logger = LoggerFactory
            .getLogger(ProcessImageAndQueryJob.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -6037268622673246125L;

    private static class TableComparatorByScore implements
            Comparator<MatchTable>, Serializable {

        /**
		 * 
		 */
        private static final long serialVersionUID = -4149880383456969176L;

        @Override
        public int compare(MatchTable o1, MatchTable o2) {
            // put null items at the end
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            float result = o1.getScore() - o2.getScore();
            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private final MatchTable[] tables;
    private int count;
    private final MultiArchiveIndex searchScope;
    private final FloatImage queryImage;
    private final TableComparatorByScore comparator = new TableComparatorByScore();
    private final int batchSize;
    private final int results;
    private int batches;
    private ActivityIdentifier[] destinations;

    public ProcessImageAndQueryJob(FloatImage queryImage,
            MultiArchiveIndex searchScope, int results, int batchSize,
            ActivityIdentifier... destinations) {
        super(new CBIRActivityContext(ContextStrings.QUERY_INITIATOR, true),
                true, true);
        this.tables = new MatchTable[results * 2];
        this.searchScope = searchScope;
        this.queryImage = queryImage;
        this.results = results;
        this.batchSize = batchSize;
        batches = 0;
        this.destinations = destinations;
    }

    @Override
    public void initialize() throws Exception {
        // long time = -System.nanoTime();
        if (logger.isDebugEnabled()) {
            logger.debug("INIT");
        }
        if (tables.length == 0 || searchScope.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Empty Query --> DONE");
            }
            finish();
        } else {
            // fetch query data
            // getExecutor().submit(new CalculateEndmembers(queryImage,
            // Config.QUERY_INITIATOR_REPOSITORY, identifier()));
            getExecutor().submit(
                    new CalculateEndmembers(queryImage, identifier()));
            suspend();
        }
        // time += System.nanoTime();
        // System.out.println("QueryJob.initialize() took " + time/1000 +
        // "micros");
    }

    private void startQuery(Metadata query) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating QueryBatches");
        }
        for (Entry<HashSet<String>, Set<ImageIdentifier>> databaseEntry : searchScope
                .getElementsByArchive().entrySet()) {
            ActivityContext context = createContextForBatch(
                    databaseEntry.getKey(), true);
            Set<ImageIdentifier> images = new HashSet<ImageIdentifier>();

            for (ImageIdentifier imageID : databaseEntry.getValue()) {
                images.add(imageID);
                if (images.size() >= batchSize) {
                    QueryBatch queryBatch = new QueryBatch(identifier(),
                            images, context, query, results, comparator);
                    getExecutor().submit(queryBatch);
                    batches++;
                    images = new HashSet<ImageIdentifier>();
                }
            }
            if (images.size() > 0) {
                QueryBatch searchJob = new QueryBatch(identifier(), images,
                        context, query, results, comparator);
                getExecutor().submit(searchJob);
                batches++;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(batches + " batches created");
        }
    }

    @Override
    public void process(Event e) throws Exception {
        if (e.data instanceof MatchTable[]) {
            // long time = -System.nanoTime();
            if (logger.isDebugEnabled()) {
                logger.debug("Received a resultBatch");
            }
            boolean done = processBatch((MatchTable[]) e.data);
            if (done) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Received all results --> DONE");
                }
                finish();
            } else {
                suspend();
            }
        } else if (e.data instanceof Metadata) {
            Metadata md = (Metadata) e.data; 
            if (md.getEndmembers() == null) {
                // FIXME NFindr failed, cannot do query
                if (logger.isDebugEnabled()) {
                    logger.debug("Endmember Extraction for " + md.getImageID().getPrettyName() + " failed");
                }
                finish();
            } else {
                startQuery(md);
                suspend();
            }
        }
    }

    /**
     * 
     * @param resultBatch
     * @return true when done
     */
    private boolean processBatch(MatchTable[] resultBatch) {
        // overwrite the second half of the array and sort it, putting the best
        // 'results' solutions to the front
        System.arraycopy(resultBatch, 0, tables, results, resultBatch.length);
        Arrays.sort(tables, comparator);

        // System.arraycopy(results, 0, tables, count, results.length);
        count++;
        return (count == batches);
    }

    @Override
    public void cleanup() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("cleanup()");
        }
        // send only the first "results" tables
        MatchTable[] msg = Arrays.copyOf(tables, results);

        for (ActivityIdentifier dest : destinations) {
            getExecutor().send(new QueryResultEvent(identifier(), dest, msg));
        }
    }

    @Override
    public void cancel() throws Exception {
        // empty
    }

    @Override
    public String toString() {
        return "QueryJob(" + identifier() + ", " + tables.length + ")";
    }

    private ActivityContext createStoreWorkerContext(String[] stores,
            boolean interactive) {
        if (stores.length == 1) {
            return new CBIRActivityContext(
                    ContextStrings.createForStoreWorker(stores[0]), interactive);
        }
        CBIRActivityContext[] contexts = new CBIRActivityContext[stores.length];
        int i = 0;
        for (String store : stores) {
            contexts[i] = new CBIRActivityContext(
                    ContextStrings.createForStoreWorker(store), interactive);
            i++;
        }

        return new OrActivityContext(contexts, false);
    }

    private ActivityContext createContextForBatch(Set<String> stores,
            boolean interactive) {

        return createStoreWorkerContext(
                stores.toArray(new String[stores.size()]), interactive);
    }

}
