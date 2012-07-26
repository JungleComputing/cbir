package cbir.frontend;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.Config;
import cbir.backend.MultiArchiveIndex;
import cbir.backend.repository.RepositoryExecutor;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.frontend.activities.GetDataAndQueryJob;
import cbir.frontend.activities.GetImageCollection;
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

	private volatile MultiArchiveIndex index;
	private long indexUpdateTime;

	public QueryInitiator(String repositoryName, String[] stores,
			RepositoryOperations ops, StealPool belongsTo,
			StealPool stealsFrom, WorkerContext context, boolean useGPU) {
		super(repositoryName, ops, belongsTo, stealsFrom, context, useGPU);
		this.stores = stores;
		setImageIndex(new MultiArchiveIndex());
	}
	
	@Override
	public void run() {
		//Start a thread that periodically updates the index first
//		Thread indexUpdater = new Thread() {
//			long updateStartTime = 0;
//			public void run() {
//				while (true) {
//					submit(new GetImageCollection(null, getStores()));
//					updateStartTime = System.currentTimeMillis();
//					for(long update = getIndexUpdateTime(); update < updateStartTime || update + 10000 < System.currentTimeMillis();) {
//						try {
//							Thread.sleep(10000);
//						} catch (InterruptedException e) {
//							//ignore
//						}
//					}
//				}
//			}
//		};
//		indexUpdater.setDaemon(true);
//		indexUpdater.start();

		super.run();
	}

	public void query(FloatImage queryData, MultiArchiveIndex searchScope,
			ActivityIdentifier destination) {
		if (searchScope == null) {
			searchScope = getImageIndex();
		}
		
		ProcessImageAndQueryJob q = new ProcessImageAndQueryJob(queryData,
				searchScope, Config.nResults, Config.batchSize, destination);
		submit(q);
	}

	public void query(ImageIdentifier imageID, MultiArchiveIndex searchScope,
			ActivityIdentifier destination) {
		if (searchScope == null) {
			searchScope = getImageIndex();
		}

		String[] stores = getImageIndex().getStoresFor(imageID);
		
		GetDataAndQueryJob job = new GetDataAndQueryJob(imageID,
				stores, searchScope,
				Config.nResults, Config.batchSize, destination);
		submit(job);
	}

//	public void getMetadata(String imageURI, ActivityIdentifier destination) {
//		submit(new CalculateEndmembers(imageURI,
//				Config.QUERY_INITIATOR_REPOSITORY, destination));
//	}

	public void getIndex(ActivityIdentifier destination) {
		submit(new GetImageCollection(destination, getStores()));
	}

	public void getImage(ImageIdentifier imageID, String[] stores,
			ActivityIdentifier destination) {
		submit(new GetImageFromStore(imageID, stores, destination));
	}
	
	public void getImage(ImageIdentifier imageID, ActivityIdentifier destination) {
		String[] stores = index.getStoresForOriginalImage(imageID);
		submit(new GetImageFromStore(imageID, stores, destination));
	}

	public void getImagePreview(ImageIdentifier imageID, int red, int green, int blue,
			String[] stores, ActivityIdentifier destination) {
		submit(new GetPreviewFromStore(imageID, red, green, blue, stores,
				destination));
	}

	private String[] getStores() {
		return stores;
	}

	// private MatchTable[] query(String query, MultiArchiveIndex searchScope) {
	// if (searchScope == null) {
	// searchScope = getImageIndex();
	// }
	// Metadata md = getMetadata(query);
	// System.out.println("Got Query Metadata!");
	//
	// LaunchQuery q = new LaunchQuery(md, searchScope, Config.nResults,
	// Config.batchSize);
	// submit(q);
	// System.out.println("Query Started...");
	// return q.waitForResults();
	// }
	//
	// private MatchTable[] query(Metadata queryData, MultiArchiveIndex
	// searchScope) {
	// LaunchQuery q = new LaunchQuery(queryData, searchScope,
	// Config.nResults, Config.batchSize);
	// submit(q);
	// System.out.println("Query Started...");
	// return q.waitForResults();
	// }

	// private Metadata getMetadata(String imageURI) {
	// SingleEventCollector querySec = new SingleEventCollector(
	// new UnitActivityContext(ContextStrings.QUERY_INITIATOR));
	// submit(new CalculateEndmembers(imageURI,
	// Config.QUERY_INITIATOR_REPOSITORY, submit(querySec)));
	//
	// return (Metadata) querySec.waitForEvent().data;
	// }

	// private MultiArchiveIndex getSearchSpace() {
	// SingleEventCollector scopeSec = new SingleEventCollector(
	// new UnitActivityContext(ContextStrings.QUERY_INITIATOR));
	// submit(new GetImageCollection(submit(scopeSec), stores));
	// MultiArchiveIndex searchScope = (MultiArchiveIndex) scopeSec
	// .waitForEvent().data;
	// System.out.println("Got SearchScope: " + searchScope.size()
	// + " images!");
	// return searchScope;
	// }

	private synchronized MultiArchiveIndex getImageIndex() {
		return index;
	}

	public synchronized void setImageIndex(MultiArchiveIndex index) {
		this.index = index;
		indexUpdateTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) {
			logger.debug("***");
			logger.debug("Store index updated");
			logger.debug("timestamp:" + indexUpdateTime);
			logger.debug("now containing " + index.size() + " images");
			logger.debug("***");
		}
	}

	public long getIndexUpdateTime() {
		return indexUpdateTime;
	}

}
