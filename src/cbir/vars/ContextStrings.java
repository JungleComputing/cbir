package cbir.vars;

import cbir.Config;

public class ContextStrings {
//	public static final String DatabaseCPU = "Database_C";
//	public static final String DatabaseGPU = "Database_G";
	public static final String CPU_KERNEL = "CPU";
	public static final String GPU_KERNEL = "GPU";
	
//	public static final String QUERY = "QUERY";
	public static final String QUERY_INITIATOR = createForRepository(Config.QUERY_INITIATOR_REPOSITORY);
//	public static final String MANAGEMENT = "Management";
	
	public static final String GUI = "GUI";
	
	public static String createForRepository(String repositoryName) {
		return "REP<" + repositoryName + ">";
	}
	
	public static String createForRepositoryMaster(String repositoryName) {
		return "REPMASTER<" + repositoryName + ">";
	}
	
	public static String createForStore(String storeName) {
		return "STORE<" + storeName + ">";
	}
	
	public static String createForStoreWorker(String storeName) {
		return "STOREWORKER<" + storeName + ">";
	}
	
	public static String createForCache(String storeName) {
		return "CACHE<" + storeName + ">";
	}
}
