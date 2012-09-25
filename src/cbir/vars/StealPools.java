package cbir.vars;

import cbir.RepositoryDescriptor;
import ibis.constellation.StealPool;

public class StealPools {
    public static StealPool RepositoryPool(RepositoryDescriptor... repositories) {
        if (repositories.length == 1) {
            return new StealPool("REP<" + repositories[0].getName() + ">");
        } else {
            StealPool[] sps = new StealPool[repositories.length];
            for (int i = 0; i < repositories.length; i++) {
                sps[i] = new StealPool("REP<" + repositories[i].getName() + ">");
            }
            return new StealPool(sps);
        }
    }

    public static StealPool RepositoryClientPool(RepositoryDescriptor... repositories) {
        if (repositories.length == 1) {
            return new StealPool("REPCLIENT<" + repositories[0].getName() + ">");
        } else {
            StealPool[] sps = new StealPool[repositories.length];
            for (int i = 0; i < repositories.length; i++) {
                sps[i] = new StealPool("REPCLIENT<" + repositories[i].getName() + ">");
            }
            return new StealPool(sps);
        }
    }
    
    public static StealPool RepositoryClientPool(String... repositoryNames) {
        if (repositoryNames.length == 1) {
            return new StealPool("REPCLIENT<" + repositoryNames[0] + ">");
        } else {
            StealPool[] sps = new StealPool[repositoryNames.length];
            for (int i = 0; i < repositoryNames.length; i++) {
                sps[i] = new StealPool("REPCLIENT<" + repositoryNames[i] + ">");
            }
            return new StealPool(sps);
        }
    }

    public static StealPool RepositoryMasterPool(RepositoryDescriptor... repositories) {
        if (repositories.length == 1) {
            return new StealPool("REPMASTER<" + repositories[0].getName() + ">");
        } else {
            StealPool[] sps = new StealPool[repositories.length];
            for (int i = 0; i < repositories.length; i++) {
                sps[i] = new StealPool("REPMASTER<" + repositories[i].getName() + ">");
            }
            return new StealPool(sps);
        }
    }

    // public static StealPool RepositoryMasterPool(String repositoryName) {
    // return new StealPool("REPMASTER<" + repositoryName + ">");
    // }

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
