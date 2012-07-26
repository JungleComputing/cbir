package cbir.vars;

import ibis.constellation.StealPool;

public class StealPools {
	public static StealPool RepositoryPool(String repositoryName) {
		return new StealPool("REP<" + repositoryName + ">");
	}
	
	public static StealPool RepositoryClientPool(String repositoryName) {
		return new StealPool("REPCLIENT<" + repositoryName + ">");
	}
	
//	public static StealPool RepositoryMasterPool(String repositoryName) {
//		return new StealPool("REPMASTER<" + repositoryName + ">");
//	}
	
	public static StealPool MetadataStore(String storeName) {
		return new StealPool("METASTORE<" + storeName + ">");
	}
	
	public static StealPool MetadataStoreClients(String storeName) {
		return new StealPool("METASTORE_CLIENT<" + storeName + ">");
	}
	
	public static final StealPool QueryPool = new StealPool("QUERY");
	public static final StealPool CommandPool = new StealPool("COMMAND");
	public static final StealPool None = StealPool.NONE;
	
}
