package cbir.frontend;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Executor;
import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.Config;
import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
import cbir.backend.repository.RepositoryExecutor;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.events.StoreIndexUpdateEvent;
import cbir.frontend.activities.GetDataAndQueryJob;
import cbir.frontend.activities.GetHeaderFromStore;
import cbir.frontend.activities.GetImageCollectionAndUpdates;
import cbir.frontend.activities.GetImageFromStore;
import cbir.frontend.activities.GetPreviewFromStore;
import cbir.frontend.activities.ProcessImageAndQueryJob;

/**
 * @author Timo van Kessel
 * 
 */
public class QueryInitiator extends RepositoryExecutor {

    private static final Logger logger = LoggerFactory
            .getLogger(QueryInitiator.class);

    private static final long serialVersionUID = -2052758765902363927L;

    private final String[] stores;

    private MultiArchiveIndex index;
    private long indexUpdateTime;
    private ArrayList<ActivityIdentifier> indexUpdateListeners;

    public QueryInitiator(String[] stores, RepositoryOperations[] ops,
            StealPool belongsTo, StealPool stealsFrom, WorkerContext context) {
        super(ops, belongsTo, stealsFrom, context);
        this.stores = stores;
        index = new MultiArchiveIndex();
        indexUpdateListeners = new ArrayList<ActivityIdentifier>();
    }

    public QueryInitiator(String[] stores, RepositoryOperations[] ops,
            StealPool belongsTo, StealPool stealsFrom, WorkerContext context,
            long cudaHandle) {
        super(ops, belongsTo, stealsFrom, context, cudaHandle);
        this.stores = stores;
        index = new MultiArchiveIndex();
        indexUpdateListeners = new ArrayList<ActivityIdentifier>();
    }
    
    @Override
    public void run() {
        submit(new GetImageCollectionAndUpdates(getStores()));
//        submit(new GetStoreIndexAndRegisterForUpdates());
        super.run();
    }

    public void query(FloatImage queryData, MultiArchiveIndex searchScope,
            long queryTimeStamp, ActivityIdentifier destination) {
        if (searchScope == null) {
            searchScope = getImageIndex();
        }

        ProcessImageAndQueryJob q = new ProcessImageAndQueryJob(queryData,
                searchScope, Config.nResults, Config.batchSize, queryTimeStamp,
                destination);
        submit(q);
    }

    public void query(ImageIdentifier imageID, MultiArchiveIndex searchScope,
            long queryTimeStamp, ActivityIdentifier destination) {
        if (searchScope == null) {
            searchScope = getImageIndex();
        }

        String[] stores = getImageIndex().getStoresFor(imageID);

        GetDataAndQueryJob job = new GetDataAndQueryJob(imageID, stores,
                searchScope, Config.nResults, Config.batchSize, queryTimeStamp,
                destination);
        submit(job);
    }

    // public void getMetadata(String imageURI, ActivityIdentifier destination)
    // {
    // submit(new CalculateEndmembers(imageURI,
    // Config.QUERY_INITIATOR_REPOSITORY, destination));
    // }

//    public void getIndex(ActivityIdentifier destination) {
//        send(new StoreIndexEvent(null, destination, getImageIndex()));
////        submit(new GetImageCollection(destination, getStores()));
//    }

    public void getImage(ImageIdentifier imageID, String[] stores,
            ActivityIdentifier destination) {
        submit(new GetImageFromStore(imageID, stores, destination));
    }

    public void getImage(ImageIdentifier imageID, ActivityIdentifier destination) {
        String[] stores = index.getStoresForOriginalImage(imageID);
        submit(new GetImageFromStore(imageID, stores, destination));
    }

    public void getHeader(ImageIdentifier imageID, String[] stores,
            ActivityIdentifier destination) {
        submit(new GetHeaderFromStore(imageID, stores, destination));
    }

    public void getHeader(ImageIdentifier imageID,
            ActivityIdentifier destination) {
        String[] stores = index.getStoresForOriginalImage(imageID);
        submit(new GetHeaderFromStore(imageID, stores, destination));
    }

    public void getImagePreview(ImageIdentifier imageID, int red, int green,
            int blue, String[] stores, ActivityIdentifier destination) {
        submit(new GetPreviewFromStore(imageID, red, green, blue, stores,
                destination));
    }

    private String[] getStores() {
        return stores;
    }

    private synchronized MultiArchiveIndex getImageIndex() {
        return index;
    }

    public  void addIndex(SingleArchiveIndex sai, Executor executor) {
        ActivityIdentifier[] indexUpdateListenersArray;
        synchronized(this) {
            indexUpdateListenersArray = new ActivityIdentifier[indexUpdateListeners.size()];
            indexUpdateListenersArray = indexUpdateListeners.toArray(indexUpdateListenersArray);  
            index.add(sai);
        }
        indexUpdateTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("***");
            logger.debug("Store index updated");
            logger.debug("timestamp:" + indexUpdateTime);
            logger.debug("now containing " + index.size() + " images");
            logger.debug("***");
        }
        for(ActivityIdentifier listener: indexUpdateListenersArray) {
            executor.send(new StoreIndexUpdateEvent(null, listener, sai));
        }
    }
    
//    public synchronized void setImageIndex(MultiArchiveIndex index) {
//        this.index = index;
//        indexUpdateTime = System.currentTimeMillis();        
//        if (logger.isDebugEnabled()) {
//            logger.debug("***");
//            logger.debug("Store index updated");
//            logger.debug("timestamp:" + indexUpdateTime);
//            logger.debug("now containing " + index.size() + " images");
//            logger.debug("***");
//        }
//    }

    public long getIndexUpdateTime() {
        return indexUpdateTime;
    }

    public synchronized void registerForIndexUpdates(ActivityIdentifier listener) {
        indexUpdateListeners.add(listener);
    }

}
