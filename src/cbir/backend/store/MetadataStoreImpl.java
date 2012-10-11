package cbir.backend.store;

import ibis.constellation.ActivityIdentifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cbir.Config;
import cbir.backend.MetadataStore;
import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
import cbir.envi.ImageIdentifier;
import cbir.events.StoreIndexUpdateEvent;
import cbir.metadata.Metadata;
import cbir.node.Node;

public class MetadataStoreImpl implements MetadataStore {
    private final HashMap<ImageIdentifier, Metadata> store;
    private final ReentrantReadWriteLock cacheLock;
    private final ReadLock readLock;
    private final WriteLock writeLock;
    private final String name;
    private final MultiArchiveIndex imageIndex;
    private HashSet<ImageIdentifier> updates;
    private HashSet<ActivityIdentifier> listeners;
    private Thread updateNotifier;

    public MetadataStoreImpl(String name) {
        store = new HashMap<ImageIdentifier, Metadata>();
        cacheLock = new ReentrantReadWriteLock(true);
        readLock = cacheLock.readLock();
        writeLock = cacheLock.writeLock();
        this.name = name;
        imageIndex = new MultiArchiveIndex();
        listeners = new HashSet<ActivityIdentifier>();
        updates = new HashSet<>();
        updateNotifier = null;
    }

    @Override
    public void enableUpdates(final Node node) {
        if (updateNotifier == null) {
            updateNotifier = new Thread() {
                @Override
                public void run() {
                    for (;;) {
                        SingleArchiveIndex updates = getUpdates();
                        if (updates.size() > 0) {
                            synchronized (listeners) {
                                for (ActivityIdentifier listener : listeners) {
                                    node.send(new StoreIndexMessage(null,
                                            listener, updates));
                                }
                            }
                        }
                        try {
                            Thread.sleep(Config.STORE_UPDATE_INTERVAL);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };
            updateNotifier.setDaemon(true);
            updateNotifier.start();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean contains(ImageIdentifier imageID) {
        readLock.lock();
        try {
            return store.containsKey(imageID);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Metadata get(ImageIdentifier imageID) {
        readLock.lock();
        try {
            return store.get(imageID);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public SingleArchiveIndex contents() {
        return new SingleArchiveIndex(getName(), contentArray());
    }

    private ImageIdentifier[] contentArray() {
        readLock.lock();
        try {
            return store.keySet().toArray(new ImageIdentifier[store.size()]);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(Metadata metadata, Set<String> locations) {
        writeLock.lock();
        try {
            store.put(metadata.getImageID(), metadata);
            imageIndex.addAll(metadata.getImageID(), locations);
            updates.add(metadata.getImageID());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void put(Metadata metadata, String... locations) {
        writeLock.lock();
        try {
            store.put(metadata.getImageID(), metadata);
            imageIndex.addAll(metadata.getImageID(), Arrays.asList(locations));
            updates.add(metadata.getImageID());
        } finally {
            writeLock.unlock();
        }
    }

    protected SingleArchiveIndex getUpdates() {
        ImageIdentifier[] updatesArray = null;
        writeLock.lock();
        try {
            updatesArray = new ImageIdentifier[updates.size()];
            updates.toArray(updatesArray);
            updates.clear();
        } finally {
            writeLock.unlock();
        }
        return new SingleArchiveIndex(name, updatesArray);
    }

    // protected Metadata remove(ImageIdentifier imageID) {
    // writeLock.lock();
    // try {
    // imageIndex.remove(imageID);
    // removals.add(imageID);
    // return store.remove(imageID);
    // } finally {
    // writeLock.unlock();
    // }
    // }

    @Override
    public int size() {
        readLock.lock();
        try {
            return store.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String[] getRepositoriesFor(ImageIdentifier imageID) {
        return imageIndex.getStoresFor(imageID);
    }

    @Override
    public void registerUpdateListener(ActivityIdentifier listener)
            throws Exception {
        if (updateNotifier == null) {
            throw new Exception("updates not enabled");
        }
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeUpdateListener(ActivityIdentifier listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
