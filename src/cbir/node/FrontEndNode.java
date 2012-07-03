package cbir.node;

import ibis.constellation.Executor;
import cbir.Cbir;
import cbir.frontend.MetadataCache;

public class FrontEndNode extends Node {

	public FrontEndNode(Executor... executors) {
		super(executors);
		
	}

	
	public static void main (String[] args) {
		int nExecutors = Runtime.getRuntime().availableProcessors();
		
		// parse command-line arguments
		String storeName = args[0];
		if(args.length > 1) {
			nExecutors = Integer.parseInt(args[1]);
		}
		
		
		Cbir cbir = new Cbir();
		MetadataCache cache = cbir.getFactory().createMetadataCache(storeName);

		Executor[] executors = new Executor[nExecutors + 1];
		for(int i = 0; i < nExecutors; i++) {
			executors[i] = cbir.getFactory().createQueryExecutor(cache, i < 2); //max 2 executors may use the GPU
		}
		executors[nExecutors] = cache.getExecutor();
		FrontEndNode node = new FrontEndNode(executors);
		node.activate();
		node.done();
	}
}
