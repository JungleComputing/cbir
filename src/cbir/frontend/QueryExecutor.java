package cbir.frontend;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;
import cbir.kernels.KernelExecutor;

/**
 * @author Timo van Kessel
 * 
 */
public class QueryExecutor extends KernelExecutor {

	private final MetadataCache cache;

	public QueryExecutor(MetadataCache cache, StealPool belongsTo,
			StealPool stealsFrom, WorkerContext context, boolean useGPU) {
		super(belongsTo, stealsFrom, context, useGPU);
		this.cache = cache;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7211029456535763333L;

	public MetadataCache getCache() {
		return cache;
	}
	
	@Override
	public void run() {
		cache.init();
		super.run();
	}

}
