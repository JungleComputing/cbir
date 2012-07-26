package cbir.backend.store;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cbir.backend.MetadataStore;
import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

public class MetadataStoreImpl implements MetadataStore {
	private final HashMap<ImageIdentifier, Metadata> store;
	private final ReentrantReadWriteLock cacheLock;
	private final ReadLock readLock;
	private final WriteLock writeLock;
	private final String name;
	private final MultiArchiveIndex imageIndex;

	public MetadataStoreImpl(String name) {
		store = new HashMap<ImageIdentifier, Metadata>();
		cacheLock = new ReentrantReadWriteLock(true);
		readLock = cacheLock.readLock();
		writeLock = cacheLock.writeLock();
		this.name = name;
		imageIndex = new MultiArchiveIndex();
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
		} finally {
			writeLock.unlock();
		}
	}

	protected Metadata remove(ImageIdentifier imageID) {
		writeLock.lock();
		try {
			imageIndex.remove(imageID);
			return store.remove(imageID);
		} finally {
			writeLock.unlock();
		}
	}
	
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
}
