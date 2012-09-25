package cbir.backend.repository;

import cbir.backend.MultiArchiveIndex;


/**
 * @author Timo van Kessel
 *
 */
public interface RepositoryMaster extends Repository {
	MultiArchiveIndex getIndex();
}
