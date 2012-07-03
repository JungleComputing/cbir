package cbir.node;

import java.net.URISyntaxException;

import org.gridlab.gat.URI;

import ibis.constellation.Executor;
import cbir.Cbir;

public class GATRepositoryNode extends Node {
	public GATRepositoryNode(Executor... executors) {
		super(executors);
	}

	public static void main(String[] args) throws URISyntaxException {

		String repositoryName = args[0];
		URI baseURI = new URI(args[1]);
		boolean master = args[2].equalsIgnoreCase("master");
		int nExecutors = Integer.parseInt(args[3]);

		Cbir cbir = new Cbir();
		Executor[] executors = new Executor[nExecutors];
		boolean useGpu = true;
		
		if (master) {
			executors[0] = cbir.getFactory().createGATRepositoryMasterExecutor(
					repositoryName, baseURI);
		} else {
			executors[0] = cbir.getFactory().createGATRepositoryExecutor(
					repositoryName, baseURI, useGpu);
			useGpu = false;
		}
		for(int i = 1; i < executors.length; i++) {
			executors[i] = cbir.getFactory().createGATRepositoryExecutor(
					repositoryName, baseURI, useGpu);
			useGpu = false;
		}
		
		
		Node node = new GATRepositoryNode(executors);
		node.activate();
		// again: nothing to do over here
		node.done();
	}
}
