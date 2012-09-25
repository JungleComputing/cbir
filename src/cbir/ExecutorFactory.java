package cbir;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;
import ibis.constellation.context.OrWorkerContext;
import ibis.constellation.context.UnitWorkerContext;

import org.gridlab.gat.URI;

import cbir.backend.MetadataStore;
import cbir.backend.repository.RepositoryExecutor;
import cbir.backend.repository.RepositoryMasterExecutor;
import cbir.backend.repository.operations.GATRepositoryOperations;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.backend.store.MetadataStoreExecutor;
import cbir.frontend.ControlExecutor;
import cbir.frontend.MetadataCache;
import cbir.frontend.QueryExecutor;
import cbir.frontend.QueryInitiator;
import cbir.frontend.cache.MetadataCacheImpl;
import cbir.vars.ContextStrings;
import cbir.vars.StealPools;

public class ExecutorFactory {
    private static final String initiatorName = "QUERY_INITIATOR";

    protected ExecutorFactory() {
    }

    public ControlExecutor createControlExecutor() {
        UnitWorkerContext context = new UnitWorkerContext(ContextStrings.GUI);

        return new ControlExecutor(StealPools.CommandPool, StealPool.NONE,
                context);
    }

    public QueryInitiator createQueryInitiator(URI baseURI, String[] stores,
            boolean useGpu, boolean receiveCommands) {
        UnitWorkerContext[] unit;
        if (useGpu) {
            unit = new UnitWorkerContext[] {
                    new UnitWorkerContext(ContextStrings.QUERY_INITIATOR),
                    new UnitWorkerContext(ContextStrings.GPU_KERNEL),
                    new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
        } else {
            unit = new UnitWorkerContext[] {
                    new UnitWorkerContext(ContextStrings.QUERY_INITIATOR),
                    new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
        }

        RepositoryOperations ops = new GATRepositoryOperations(
                new RepositoryDescriptor(initiatorName, baseURI));

        StealPool[] pools = new StealPool[stores.length + 1];
        for (int i = 0; i < stores.length; i++) {
            pools[i] = StealPools.MetadataStoreClients(stores[i]);
        }
        pools[stores.length] = StealPools.QueryPool;

        if (receiveCommands) {
            return new QueryInitiator(stores,
                    new RepositoryOperations[] { ops }, new StealPool(pools),
                    StealPools.CommandPool, new OrWorkerContext(unit, true),
                    useGpu);
        } else {
            return new QueryInitiator(stores,
                    new RepositoryOperations[] { ops }, new StealPool(pools),
                    StealPools.None, new OrWorkerContext(unit, true), useGpu);
        }
    }

    public QueryExecutor createQueryExecutor(MetadataCache cache, boolean useGpu) {

        UnitWorkerContext[] unit;
        if (useGpu) {
            unit = new UnitWorkerContext[] {
                    new UnitWorkerContext(
                            ContextStrings.createForStoreWorker(cache
                                    .storeName())),
                    new UnitWorkerContext(ContextStrings.GPU_KERNEL),
                    new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
        } else {
            unit = new UnitWorkerContext[] {
                    new UnitWorkerContext(
                            ContextStrings.createForStoreWorker(cache
                                    .storeName())),
                    new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
        }

        return new QueryExecutor(cache, StealPools.None, StealPools.QueryPool,
                new OrWorkerContext(unit, true), useGpu);
    }

    public MetadataCache createMetadataCache(String storeName) {
        return new MetadataCacheImpl(storeName,
                StealPools.MetadataStoreClients(storeName), StealPools.None,
                new UnitWorkerContext(ContextStrings.createForCache(storeName)));
    }

    public MetadataStoreExecutor createMetadataStoreExecutor(
            MetadataStore store, String... repositories) {
        StealPool stealsFrom = StealPools.MetadataStoreClients(store.getName());
        StealPool belongsTo;
        if (repositories == null || repositories.length == 0) {
            belongsTo = StealPool.NONE;
        } else {
            int reps = repositories.length + 1;
            StealPool[] rps = new StealPool[reps];
            // StealPool[] rps = new StealPool[reps * 2];
            for (int i = 0; i < repositories.length; i++) {
                rps[i] = StealPools.RepositoryClientPool(repositories[i]);
                // rps[reps + i] = StealPools
                // .RepositoryMasterPool(repositories[i]);
            }
            rps[repositories.length] = StealPools
                    .MetadataStore(store.getName());
            belongsTo = new StealPool(rps);
        }

        return new MetadataStoreExecutor(store, belongsTo, stealsFrom,
                new UnitWorkerContext(ContextStrings.createForStore(store
                        .getName())));
    }

    public RepositoryExecutor createGATRepositoryExecutor(
            RepositoryDescriptor[] repositories, boolean useGpu) {

        RepositoryOperations[] ops = new RepositoryOperations[repositories.length];
        for (int i = 0; i < repositories.length; i++) {
            ops[i] = new GATRepositoryOperations(repositories[i]);
        }

        // StealPool repositoryPool = StealPools.RepositoryPool(repositoryName);
        // StealPool repositoryClientPool = StealPools
        // .RepositoryClientPool(repositoryName);

        StealPool repositoryPool = StealPool.NONE;
        StealPool repositoryClientPool = StealPools
                .RepositoryMasterPool(repositories);

        UnitWorkerContext[] unit;
        if (useGpu) {
            unit = new UnitWorkerContext[repositories.length + 2];
            for (int i = 0; i < repositories.length; i++) {
                unit[i] = new UnitWorkerContext(
                        ContextStrings.createForRepository(repositories[i]
                                .getName()));
            }
            unit[repositories.length] = new UnitWorkerContext(
                    ContextStrings.GPU_KERNEL);
            unit[repositories.length + 1] = new UnitWorkerContext(
                    ContextStrings.CPU_KERNEL);
        } else {
            unit = new UnitWorkerContext[repositories.length + 1];
            for (int i = 0; i < repositories.length; i++) {
                unit[i] = new UnitWorkerContext(
                        ContextStrings.createForRepository(repositories[i]
                                .getName()));
            }
            unit[repositories.length] = new UnitWorkerContext(
                    ContextStrings.CPU_KERNEL);
        }
        WorkerContext context = new OrWorkerContext(unit, true);

        // return new RepositoryExecutor(repositoryName, ops, repositoryPool,
        // new StealPool(repositoryPool, repositoryClientPool), context,
        // useGpu);
        return new RepositoryExecutor(ops, repositoryPool,
                repositoryClientPool, context, useGpu);
    }

    public RepositoryMasterExecutor createGATRepositoryMasterExecutor(
            RepositoryDescriptor[] repositories) {
        RepositoryOperations[] ops = new RepositoryOperations[repositories.length];
        for (int i = 0; i < repositories.length; i++) {
            ops[i] = new GATRepositoryOperations(repositories[i]);
        }

        WorkerContext context;
        if (repositories.length == 1) {
            context = new UnitWorkerContext(
                    ContextStrings.createForRepositoryMaster(repositories[0]
                            .getName()));
        } else {
            UnitWorkerContext[] unit = new UnitWorkerContext[repositories.length];
            for (int i = 0; i < repositories.length; i++) {
                unit[i] = new UnitWorkerContext(
                        ContextStrings
                                .createForRepositoryMaster(repositories[i]
                                        .getName()));
            }
            context = new OrWorkerContext(unit, true);
        }

        return new RepositoryMasterExecutor(ops,
                StealPools.RepositoryMasterPool(repositories),
                StealPools.RepositoryClientPool(repositories), context);
    }

    // public RepositoryExecutor createArtificialRepositoryExecutor(
    // RepositoryDescriptor[] repositories, int files, int samples, int lines,
    // int bands, boolean useGpu) {
    // RepositoryOperations ops;
    // try {
    // ops = new ArtificialRepositoryOperations(repositoryName, files,
    // samples, lines, bands);
    // } catch (IOException e) {
    // e.printStackTrace();
    // return null;
    // }
    //
    // // StealPool repositoryPool = StealPools.RepositoryPool(repositoryName);
    // // StealPool repositoryClientPool = StealPools
    // // .RepositoryClientPool(repositoryName);
    //
    // StealPool repositoryPool = StealPool.NONE;
    // StealPool repositoryClientPool = StealPools
    // .RepositoryMasterPool(repositories);
    //
    // UnitWorkerContext[] unit;
    // if (useGpu) {
    // unit = new UnitWorkerContext[] {
    // new UnitWorkerContext(
    // ContextStrings.createForRepository(repositoryName)),
    // new UnitWorkerContext(ContextStrings.GPU_KERNEL),
    // new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
    // } else {
    // unit = new UnitWorkerContext[] {
    // new UnitWorkerContext(
    // ContextStrings.createForRepository(repositoryName)),
    // new UnitWorkerContext(ContextStrings.CPU_KERNEL) };
    // }
    // WorkerContext context = new OrWorkerContext(unit, true);
    //
    // return new RepositoryExecutor(new RepositoryOperations[] { ops },
    // repositoryPool, new StealPool(repositoryPool,
    // repositoryClientPool), context, useGpu);
    // }
    //
    // public RepositoryMasterExecutor createArtificialRepositoryMasterExecutor(
    // String repositoryName, int files, int samples, int lines, int bands) {
    // RepositoryOperations ops;
    //
    // try {
    // ops = new ArtificialRepositoryOperations(repositoryName, files,
    // samples, lines, bands);
    // } catch (IOException e) {
    // e.printStackTrace();
    // return null;
    // }
    //
    // return new RepositoryMasterExecutor(new RepositoryOperations[] { ops },
    // StealPool.NONE,
    // StealPools.RepositoryClientPool(repositoryName),
    // new UnitWorkerContext(
    // ContextStrings
    // .createForRepositoryMaster(repositoryName)));
    // }
}
