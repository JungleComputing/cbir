package cbir.frontend.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.constellation.Activity;
import ibis.constellation.Event;
import ibis.constellation.context.UnitActivityContext;
import cbir.backend.store.MetadataMessage;
import cbir.vars.ContextStrings;

/**
 * @author Timo van Kessel
 * 
 */
public class CacheActivity extends Activity {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheActivity.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2514703384154131547L;
	private MetadataCacheImpl cache;

	/**
	 * @param context
	 * @param restrictToLocal
	 * @param willReceiveEvents
	 */
	public CacheActivity(MetadataCacheImpl cache) {
		super(new UnitActivityContext(ContextStrings.createForStoreWorker(cache.storeName())), true, true);
		this.cache = cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		suspend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#process(ibis.constellation.Event)
	 */
	@Override
	public void process(Event e) throws Exception {
		if (e instanceof MetadataMessage) {
			logger.debug("MetadataMessage arrived, looking up data");
			cache.deliver(((MetadataMessage)e).getMetadata());
			suspend();
		} else {
			suspend();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#cleanup()
	 */
	@Override
	public void cleanup() throws Exception {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#cancel()
	 */
	@Override
	public void cancel() throws Exception {
		// empty
	}

}
