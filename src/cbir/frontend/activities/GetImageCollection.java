package cbir.frontend.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import ibis.constellation.context.UnitActivityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.MultiArchiveIndex;
import cbir.backend.store.StoreIndexMessage;
import cbir.events.StoreIndexEvent;
import cbir.frontend.QueryInitiator;
import cbir.vars.ContextStrings;

public class GetImageCollection extends Activity {

	private static final long serialVersionUID = -3375885765826899357L;

	private static final Logger logger = LoggerFactory
			.getLogger(GetImageCollection.class);

	private MultiArchiveIndex result;
	private String[] stores;
	private int expectedIndices;
	private ActivityIdentifier target;

	public GetImageCollection(ActivityIdentifier target, String... stores) {
		super(
				new UnitActivityContext(
						ContextStrings.QUERY_INITIATOR), true, true);
		this.target = target;
		this.stores = stores;
	}

	
	@Override
	public QueryInitiator getExecutor() {
		return (QueryInitiator) super.getExecutor();
	}
	
	@Override
	public void initialize() throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("INIT");
		}
		result = new MultiArchiveIndex();
		if (stores == null || stores.length == 0) {
			if(logger.isDebugEnabled()) {
				logger.debug("No stores --> done");
			}
			finish();
		} else {
			expectedIndices = stores.length;
			for (String store : stores) {
				getExecutor().submit(new GetStoreIndex(store, identifier()));
			}

			suspend();
		}
	}

	@Override
	public void process(Event e) throws Exception {
		if (e instanceof StoreIndexMessage) {
			result.add(((StoreIndexMessage) e).getIndex());
			if(logger.isDebugEnabled()) {
				logger.debug("Got storeIndex " + ((StoreIndexMessage)e).getIndex().getArchiveName());
			}
			expectedIndices--;
			if (expectedIndices == 0) {
				if(logger.isDebugEnabled()) {
					logger.debug("All storeIndices received -->  done");
				}
				finish();
			} else {
				if(logger.isDebugEnabled()) {
					logger.debug(String.format("Received %d out of %d indices", stores.length - expectedIndices, stores.length));
				}
				suspend();
			}
		} else {
			logger.warn("Received an unknown event: " + e.toString());
		}

	}

	@Override
	public void cleanup() throws Exception {
		//update our own copy
		getExecutor().setImageIndex(result);
		//send the result to the requestor
		if(target != null) {
			getExecutor().send(new StoreIndexEvent(identifier(), target, result));
		}

	}

	@Override
	public void cancel() throws Exception {
		// empty

	}

}
