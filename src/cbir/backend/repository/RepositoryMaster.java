package cbir.backend.repository;

import cbir.backend.SingleArchiveIndex;


/**
 * @author Timo van Kessel
 *
 */
public interface RepositoryMaster extends Repository {
	SingleArchiveIndex getIndex();
}
