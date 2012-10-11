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
			StealPool stealsFrom, WorkerContext context) {
		super(belongsTo, stealsFrom, context);
		this.cache = cache;
	}
	
	public QueryExecutor(MetadataCache cache, StealPool belongsTo,
                StealPool stealsFrom, WorkerContext context, long cudaHandle) {
        super(belongsTo, stealsFrom, context, cudaHandle);
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

	@Override
	protected void printStatistics() {
	        StringBuilder sb = new StringBuilder("QueryExecutor: " +
	                identifier() + "\n");
	        sb.append("--------------------------");

	        System.out.println(sb.toString());
	}
}
