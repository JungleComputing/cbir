package cbir.backend.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import ibis.constellation.context.OrActivityContext;
import cbir.backend.MetadataStore;
import cbir.backend.store.MetadataStoreExecutor;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

public abstract class StoreActivity extends Activity {

	private static final long serialVersionUID = 3023916684381764734L;

	private static ActivityContext createContext(boolean interactive, String... stores) {
		if(stores == null || stores.length == 0) {
			return null;
		} else if(stores.length ==1) {
			return new CBIRActivityContext(ContextStrings.createForStore(stores[0]), interactive);
		} else {
			CBIRActivityContext[] contexts = new CBIRActivityContext[stores.length];
			for(int i = 0; i < stores.length; i++) {
				contexts[i] = new CBIRActivityContext(ContextStrings.createForStore(stores[0]), interactive);
			}
			return new OrActivityContext(contexts);
		}
	}
	
	protected StoreActivity(boolean interactive, boolean restrictToLocal, boolean willReceiveEvents, String... stores) {
		super(createContext(interactive, stores), restrictToLocal, willReceiveEvents);
	}

	@Override
	public MetadataStoreExecutor getExecutor() {
		return (MetadataStoreExecutor) executor;
	}

	public MetadataStore getStore() {
		return getExecutor().getStore();
	}

}
