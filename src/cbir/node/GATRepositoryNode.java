package cbir.node;

import java.net.URISyntaxException;

import org.gridlab.gat.URI;

import ibis.constellation.Executor;
import cbir.Cbir;
import cbir.RepositoryDescriptor;
import cbir.kernels.cuda.Cuda;

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

        long[] handles = Cuda.getHandles();
        int nextHandle = 0;
        
        if (master) {
            executors[0] = cbir.getFactory().createGATRepositoryMasterExecutor(repositories);
        } else {
            if (handles != null && nextHandle < handles.length) {
                // one GPU-executor per Cuda device
                executors[0] = cbir.getFactory().createGATRepositoryExecutor(repositories, handles[nextHandle]);
                nextHandle++;
            } else {
                executors[0] = cbir.getFactory().createGATRepositoryExecutor(repositories);
            }
        }
        for (int k = 1; k < executors.length; k++) {
            if (handles != null && nextHandle < handles.length) {
                // one GPU-executor per Cuda device
                executors[k] = cbir.getFactory().createGATRepositoryExecutor(repositories, handles[nextHandle]);
                nextHandle++;
            } else {
                executors[k] = cbir.getFactory().createGATRepositoryExecutor(repositories);
            }
        }

        Node node = new GATRepositoryNode(executors);
        node.activate();
        // again: nothing to do over here
        node.done();
        Cuda.finish();
    }
}
