package cbir.frontend.cache;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Executor;
import ibis.constellation.SimpleExecutor;
import ibis.constellation.StealPool;
import ibis.constellation.StealStrategy;
import ibis.constellation.WorkerContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.MetadataStore;
import cbir.backend.store.InStoreMessage;
import cbir.backend.store.InStoreRequest;
import cbir.backend.store.MetadataMessage;
import cbir.backend.store.MetadataRequest;
import cbir.backend.store.MetadataStoreImpl;
import cbir.envi.ImageIdentifier;
import cbir.frontend.MetadataCache;
import cbir.frontend.activities.StoreCommunicator;
import cbir.metadata.Metadata;

/**
 * @author Timo van Kessel
 * 
 */
public class MetadataCacheImpl extends SimpleExecutor implements MetadataCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2133444072746256149L;

	private static final Logger logger = LoggerFactory
			.getLogger(MetadataCacheImpl.class);

	private MetadataStore localStore;
	private HashMap<ImageIdentifier, List<ActivityIdentifier>> pendingRequests;
	private ActivityIdentifier storeCommunicationActivity;
	private ActivityIdentifier cacheActivity;
	private final String storeName;

	public MetadataCacheImpl(String storeName, StealPool belongsTo,
			StealPool stealFrom, WorkerContext c) {
		super(belongsTo, stealFrom, c, StealStrategy.SMALLEST,
				StealStrategy.BIGGEST, StealStrategy.BIGGEST);
		localStore = new MetadataStoreImpl("cache<" + storeName + ">");
		pendingRequests = new HashMap<ImageIdentifier, List<ActivityIdentifier>>();
		this.storeName = storeName;
		cacheActivity = null;
	}

	@Override
	public String storeName() {
		return storeName;
	}

	@Override
	public Executor getExecutor() {
		return this;
	}

	@Override
	public synchronized void init() {
		while(cacheActivity == null || storeCommunicationActivity == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				//ignore
			}
		}
	}
	
	@Override
	public Metadata getFromCache(ImageIdentifier imageID) {
		return localStore.get(imageID);
	}

	@Override
	public boolean inCache(ImageIdentifier imageID) {
		return localStore.contains(imageID);
	}

	@Override
	public ImageIdentifier[] cacheContents() {
		return localStore.contents().getImageIDs();
	}

	@Override
	public void get(ImageIdentifier imageID, ActivityIdentifier target) {
		Metadata m = localStore.get(imageID);
		if (m == null) {
			List<ActivityIdentifier> pendingList = null;
			synchronized (pendingRequests) {
				pendingList = pendingRequests.get(imageID);
				if (pendingList == null) {
					// we do not have a pending request yet: create it
					pendingList = new LinkedList<ActivityIdentifier>();
					pendingRequests.put(imageID, pendingList);
					send(new MetadataRequest(cacheActivity,
							storeCommunicationActivity, imageID));
				}
				pendingList.add(target);
			}
		} else {
			send(new MetadataMessage(null, target, m));
		}
	}

	@Override
	public void inStore(ImageIdentifier imageID, ActivityIdentifier target) {
		if (localStore.contains(imageID)) {
			send(new InStoreMessage(null, target, imageID, true));
		} else {
			// we are not interested in the reply here, let the store reply to
			// the target immediately
			send(new InStoreRequest(target, storeCommunicationActivity, imageID));
		}

	}

	protected void deliver(Metadata metadata) {
		// add data to cache
		localStore.put(metadata, storeName);

		// deliver data to all waiting activities
		List<ActivityIdentifier> targets;
		synchronized (pendingRequests) {
			targets = pendingRequests.remove(metadata.getImageID());
		}
		if (targets == null) {
			// huh?
			logger.warn("No targets for newly acquired metadata");
			return;
		}
		for (ActivityIdentifier target : targets) {
			send(new MetadataMessage(null, target, metadata));
		}
	}

	@Override
	public void run() {
		synchronized (this) {
			cacheActivity = submit(new CacheActivity(this));
			storeCommunicationActivity = submit(new StoreCommunicator(storeName));
			notifyAll();	
		}
		super.run();
	}
}
