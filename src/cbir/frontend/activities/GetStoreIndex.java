package cbir.frontend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.SingleArchiveIndex;
import cbir.backend.activities.StoreActivity;
import cbir.backend.store.StoreIndexMessage;

public class GetStoreIndex extends StoreActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(GetStoreIndex.class);

	private static final long serialVersionUID = -3375885765826899357L;

	private final ActivityIdentifier target;

	protected GetStoreIndex(String storeName, ActivityIdentifier target) {
		super(true, false, false, storeName);
		this.target = target;
	}

	@Override
	public void initialize() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("INIT: sending index to target");

		}
		if (target != null) {
			SingleArchiveIndex contents = getStore().contents();
			if (logger.isDebugEnabled()) {
				logger.debug("INIT: sending index to target: " + contents.size() + " images");

			}
			getExecutor().send(
					new StoreIndexMessage(identifier(), target, contents));
		}
		finish();
	}

	@Override
	public void process(Event e) throws Exception {
		// empty
	}

	@Override
	public void cleanup() throws Exception {
		// empty

	}

	@Override
	public void cancel() throws Exception {
		// empty

	}

}
