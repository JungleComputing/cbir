package cbir.node;

import java.net.URISyntaxException;

import org.gridlab.gat.URI;

import ibis.constellation.Executor;
import cbir.Cbir;
import cbir.RepositoryDescriptor;

public class GATRepositoryNode extends Node {
    public GATRepositoryNode(Executor... executors) {
        super(executors);
    }

    public static void main(String[] args) throws URISyntaxException {

        int i = 0;
        int nRepositories = Integer.parseInt(args[i++]);
        RepositoryDescriptor[] repositories = new RepositoryDescriptor[nRepositories];
        for(int j = 0; j < nRepositories; j++) {
            String repositoryName = args[i++];
            URI baseURI = new URI(args[i++]);    
            repositories[j] = new RepositoryDescriptor(repositoryName, baseURI);
        }
        
        boolean master = args[i++].equalsIgnoreCase("master");
        int nExecutors = Integer.parseInt(args[i++]);

        Cbir cbir = new Cbir();
        Executor[] executors = new Executor[nExecutors];
        boolean useGpu = true;

        if (master) {
            executors[0] = cbir.getFactory().createGATRepositoryMasterExecutor(repositories);
        } else {
            executors[0] = cbir.getFactory().createGATRepositoryExecutor(repositories, useGpu);
            useGpu = false;
        }
        for (int k = 1; k < executors.length; k++) {
            executors[k] = cbir.getFactory().createGATRepositoryExecutor(
                    repositories, useGpu);
            useGpu = false;
        }

        Node node = new GATRepositoryNode(executors);
        node.activate();
        // again: nothing to do over here
        node.done();
    }
}
