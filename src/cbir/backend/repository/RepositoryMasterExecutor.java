package cbir.backend.repository;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;

import java.io.IOException;

import cbir.backend.SingleArchiveIndex;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel
 * 
 */
public class RepositoryMasterExecutor extends RepositoryExecutor 
	implements RepositoryMaster {
	
	private volatile SingleArchiveIndex index;

	public RepositoryMasterExecutor(String repositoryName, RepositoryOperations ops, StealPool belongsTo, StealPool stealsFrom,
			WorkerContext context) {
		super(repositoryName, ops, belongsTo, stealsFrom, context, false);
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
	public SingleArchiveIndex getIndex() {
		if(index == null) {
			synchronized(this) {
				while(index == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
		}
		return new SingleArchiveIndex(index);
	}

	@Override
	public void run() {
		ImageIdentifier[] imageUUIDs;
		try {
			imageUUIDs = ops.contents();
		} catch (IOException e) {
			imageUUIDs = null;
			e.printStackTrace();
		}
		index = new SingleArchiveIndex(getName(), imageUUIDs);
		synchronized(this) {
			notifyAll();
		}
		super.run();
	}

}
