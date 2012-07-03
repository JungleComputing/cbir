package cbir.backend.store;

import ibis.constellation.SimpleExecutor;
import ibis.constellation.StealPool;
import ibis.constellation.StealStrategy;
import ibis.constellation.WorkerContext;
import cbir.backend.MetadataStore;

/**
 * @author Timo van Kessel
 * 
 */
public class MetadataStoreExecutor extends SimpleExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5288282323082722024L;
	private final MetadataStore store;

	public MetadataStoreExecutor(MetadataStore store, StealPool belongsTo,
			StealPool stealFrom, WorkerContext c) {
		super(belongsTo, stealFrom, c, StealStrategy.SMALLEST,
				StealStrategy.BIGGEST, StealStrategy.BIGGEST);
		this.store = store;
	}

	public MetadataStore getStore() {
		return store;
	}
	
	@Override
	public void run() {
		super.run();
	}
}
