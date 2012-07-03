package cbir.frontend.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.MatchTable;
import cbir.backend.store.MetadataMessage;
import cbir.envi.ImageIdentifier;
import cbir.frontend.MetadataCache;
import cbir.kernels.activities.MatchingActivity;
import cbir.metadata.Metadata;

public class QueryBatch extends QueryActivity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8202459252237330231L;

	private static final Logger logger = LoggerFactory
			.getLogger(QueryBatch.class);

	private ActivityIdentifier parent;
	private Metadata query;
	private Set<ImageIdentifier> imageIDs;
	private int receivedResults;
	private MatchTable[] results;
	private int nResults;
	private final Comparator<MatchTable> comparator;

	public QueryBatch(ActivityIdentifier parent, Set<ImageIdentifier> imageIDs,
			ActivityContext context, Metadata query, int nResults,
			Comparator<MatchTable> comparator) {
		super(context, false, true);
		this.query = query;
		this.imageIDs = imageIDs;
		this.parent = parent;
		receivedResults = 0;
		results = null;
		this.nResults = nResults;
		this.comparator = comparator;
		if(logger.isDebugEnabled()) {
			logger.debug("Context: " + getContext());
		}
	}

	@Override
	public void initialize() {
		// long time = -System.nanoTime();
		MetadataCache cache = getExecutor().getCache();
		results = new MatchTable[imageIDs.size()];

		if (logger.isDebugEnabled()) {
			logger.debug("INIT: start individual match operations");
		}
		for (ImageIdentifier imageID : imageIDs) {
			Metadata metadata = cache.getFromCache(imageID);
			if (metadata == null) {
				// metadata not in cache, we have to do it the slow way
				cache.get(imageID, identifier());

			} else {
				// got metadata from cache
				if(logger.isDebugEnabled()) {
					logger.debug("Got metadata from Cache");
				}
				startMatch(metadata);

			}

		}

		suspend();
		// time += System.nanoTime();
		// System.out.println("DatabaseSearchJob.intitialize() took " +
		// time/1000 + "micros");
	}

	private void startMatch(Metadata md) {
		Activity a = new MatchingActivity(query.getEndmembers(),
				md.getEndmembers(), identifier());
		getExecutor().submit(a);
	}

	@Override
	public void process(Event e) throws Exception {
		// long time = -System.nanoTime();
		if (e instanceof MetadataMessage) {
			if(logger.isDebugEnabled()) {
				logger.debug("Got metadata from Store");
			}
			startMatch(((MetadataMessage) e).getMetadata());
			suspend();
		} else if (e.data instanceof MatchTable) {
			if(logger.isDebugEnabled()) {
				logger.debug("Got match result");
			}
			results[receivedResults] = (MatchTable) e.data;
			receivedResults++;
			if (receivedResults == imageIDs.size()) {
				Arrays.sort(results, comparator);
				if (results.length > nResults) {
					results = Arrays.copyOf(results, nResults);
				}

				getExecutor().send(new Event(identifier(), parent, results));
				if(logger.isDebugEnabled()) {
					logger.debug("All results received --> DONE");
				}
				finish();
			} else {
				suspend();
			}
		} else {
			logger.debug("Unexpected event");
			suspend();
		}
		// time += System.nanoTime();
		// System.out.println("DatabaseSearchJob.process() took " + time/1000 +
		// "micros");

	}

	@Override
	public void cancel() {

	}

	@Override
	public void cleanup() throws Exception {
		// Nothing
	}

}
