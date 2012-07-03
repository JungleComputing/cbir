package cbir.frontend.activities;

import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.activities.StoreActivity;
import cbir.backend.store.InStoreMessage;
import cbir.backend.store.InStoreRequest;
import cbir.backend.store.MetadataMessage;
import cbir.backend.store.MetadataRequest;
import cbir.backend.store.StoreIndexMessage;
import cbir.backend.store.StoreIndexRequest;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

/**
 * @author Timo van Kessel
 *
 */
public class StoreCommunicator extends StoreActivity {

	private static final Logger logger = LoggerFactory
	.getLogger(StoreCommunicator.class);
	
	private static final long serialVersionUID = -2331127797237531611L;

	/**
	 * @param storeName
	 */
	public StoreCommunicator(String store) {
		super(false, true, store);
	}

	/* (non-Javadoc)
	 * @see ibis.constellation.Activity#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		suspend();
	}

	/* (non-Javadoc)
	 * @see ibis.constellation.Activity#process(ibis.constellation.Event)
	 */
	@Override
	public void process(Event e) throws Exception {
		if(e instanceof InStoreRequest) {
			if(logger.isDebugEnabled()) {
				logger.debug("InStoreRequest");
			}
			ImageIdentifier imageID = ((InStoreRequest)e).getImageID();
			getExecutor().send(new InStoreMessage(identifier(), e.source, imageID, getStore().contains(imageID)));
			suspend();
		} else if(e instanceof MetadataRequest) {
			ImageIdentifier imageID = ((MetadataRequest)e).getImageID();
			if(logger.isDebugEnabled()) {
				logger.debug("MetaDataRequest: " + imageID);
			}
			Metadata md = getStore().get(imageID);
			if(md == null) {
				System.out.println("Metadata for " + imageID + " == null!!");
				throw new Error("Metadata for " + imageID + " == null!!");

			}
			getExecutor().send(new MetadataMessage(identifier(), e.source, md));
			suspend();
		} else if(e instanceof StoreIndexRequest) {
			if(logger.isDebugEnabled()) {
				logger.debug("StoreIndexRequest");
			}
			getExecutor().send(new StoreIndexMessage(identifier(), e.source, getStore().contents()));
			suspend();
		} else {
			logger.warn("unknown event received: " + e.data);
			suspend();
		}

	}

	/* (non-Javadoc)
	 * @see ibis.constellation.Activity#cleanup()
	 */
	@Override
	public void cleanup() throws Exception {

	}

	/* (non-Javadoc)
	 * @see ibis.constellation.Activity#cancel()
	 */
	@Override
	public void cancel() throws Exception {

	}

}
