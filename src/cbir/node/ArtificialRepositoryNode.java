package cbir.node;

import ibis.constellation.Executor;
import cbir.Cbir;

public class ArtificialRepositoryNode extends Node {
	public ArtificialRepositoryNode(Executor... executors) {
		super(executors);
	}

	public static void main(String[] args) {

		String repositoryName = args[0];
		int files = Integer.parseInt(args[1]);
		int samples = Integer.parseInt(args[2]);
		int lines = Integer.parseInt(args[3]);
		int bands = Integer.parseInt(args[4]);
		boolean master = args[5].equalsIgnoreCase("master");
		int nExecutors = Integer.parseInt(args[6]);

		Cbir cbir = new Cbir();
		Executor[] executors = new Executor[nExecutors];
		boolean useGpu = true;
		
		if (master) {
			executors[0] = cbir.getFactory().createArtificialRepositoryMasterExecutor(
					repositoryName, files, samples, lines, bands);
		} else {
			executors[0] = cbir.getFactory().createArtificialRepositoryExecutor(
					repositoryName, files, samples, lines, bands, useGpu);
			useGpu = false;
		}
		for(int i = 1; i < executors.length; i++) {
			executors[i] = cbir.getFactory().createArtificialRepositoryExecutor(
					repositoryName, files, samples, lines, bands, useGpu);
			useGpu = false;
		}
		
		
		Node node = new ArtificialRepositoryNode(executors);
		node.activate();
		// again: nothing to do over here
		node.done();
	}
}
