package cbir.backend.repository;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;

import java.io.IOException;

import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel
 * 
 */
public class RepositoryMasterExecutor extends RepositoryExecutor implements
        RepositoryMaster {

    private volatile MultiArchiveIndex index;

    public RepositoryMasterExecutor(RepositoryOperations[] ops,
            StealPool belongsTo, StealPool stealsFrom, WorkerContext context) {
        super(ops, belongsTo, stealsFrom, context);
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -7968251457692364071L;

    /*
     * (non-Javadoc)
     * 
     * @see cbir.backend.repository.RepositoryMaster#getIndex()
     */
    @Override
    public MultiArchiveIndex getIndex() {
        if (index == null) {
            synchronized (this) {
                while (index == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        }
        return new MultiArchiveIndex(index);
    }

    @Override
    public void run() {
        index = new MultiArchiveIndex();

        ImageIdentifier[] imageUUIDs;
        for (RepositoryOperations op : ops) {
            try {
                imageUUIDs = op.contents();
            } catch (IOException e) {
                imageUUIDs = null;
                e.printStackTrace();
            }
            index.add(new SingleArchiveIndex(op.getRepositoryName(), imageUUIDs));
        }
        synchronized (this) {
            notifyAll();
        }
        super.run();
    }

}
