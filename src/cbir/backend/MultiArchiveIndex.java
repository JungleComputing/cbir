package cbir.backend;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import cbir.envi.ImageIdentifier;



public class MultiArchiveIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4561953266859485804L;

	private HashMap<ImageIdentifier, Set<String>> UUIDIndex;
	// keys: image UUIDs
	// values: set of archives the image is located
	
	private HashMap<Set<String>, Set<ImageIdentifier>> elementsByArchive;
	// keys: set of stores
	// values: images present in all stores of the key
	
	private HashSet<String> archives;
	
	public MultiArchiveIndex() {
		UUIDIndex = new HashMap<ImageIdentifier, Set<String>>();
		elementsByArchive = new HashMap<Set<String>, Set<ImageIdentifier>>();
		archives = new HashSet<String>();
	}

	public void add(ImageIdentifier imageID, String store) {
		// retrieve previously known stores containing this image
		Set<String> stores = UUIDIndex.get(imageID);
		if (stores == null) {
			// It is a new image, create a new entry
			stores = new HashSet<String>();
			UUIDIndex.put(imageID, stores);
		} else {
			// remove reference from old location at imagesByStore
			Set<ImageIdentifier> dbs = elementsByArchive.get(stores);
			dbs.remove(imageID);
			if(dbs.isEmpty()) {
				elementsByArchive.remove(stores);
			}
		}
		// add new store to stores
		stores.add(store);

		// add to new indices
		Set<ImageIdentifier> imageSet = elementsByArchive.get(stores);
		if (imageSet == null) {
			imageSet = new HashSet<ImageIdentifier>();
			elementsByArchive.put(new HashSet<String>(stores), imageSet);
			
			//register store name, as we might not know this store yet
			archives.add(store);
		}
		imageSet.add(imageID);
	}
	
	public void addAll(ImageIdentifier imageID, Collection<String> newStores) {
		// retrieve previously known stores containing this image
		Set<String> stores = UUIDIndex.get(imageID);
		if (stores == null) {
			// It is a new image, create a new entry
			stores = new HashSet<String>();
			UUIDIndex.put(imageID, stores);
		} else {
			// remove reference from old location at imagesByStore
			Set<ImageIdentifier> dbs = elementsByArchive.get(stores);
			dbs.remove(imageID);
			if(dbs.isEmpty()) {
				elementsByArchive.remove(stores);
			}
		}
		// add new stores to stores
		stores.addAll(newStores);

		// add to new indices
		Set<ImageIdentifier> imageSet = elementsByArchive.get(stores);
		if (imageSet == null) {
			imageSet = new HashSet<ImageIdentifier>();
			elementsByArchive.put(new HashSet<String>(stores), imageSet);
			
			//register store names, as we might not know all new stores yet
			for(String store: stores) {
				archives.add(store);
			}
		}
		imageSet.add(imageID);
	}

	public int size() {
		return UUIDIndex.size();
	}
	
	public boolean remove(ImageIdentifier imageID) {
		Set<String> stores = UUIDIndex.remove(imageID);
		if(stores == null) {
			//image not present in store
			return false;
		}
		// also remove images from the ImagesBystore
		elementsByArchive.get(stores).remove(imageID);
		
		return true;
	}
	
	public String[] getStoresFor(ImageIdentifier imageID) {
		Set<String> stores = UUIDIndex.get(imageID);
		if(stores == null) {
			return new String[0];
		} else {
			return stores.toArray(new String[stores.size()]);
		}
	}
	
	public String[] getStoresForOriginalImage(ImageIdentifier imageID) {
		Set<String> stores = UUIDIndex.get(new ImageIdentifier(imageID.getBaseImageUuid()));
		if(stores == null) {
			return new String[0];
		} else {
			return stores.toArray(new String[stores.size()]);
		}
	}

	public HashMap<Set<String>, Set<ImageIdentifier>> getElementsByArchive() {
		return elementsByArchive;
	}
	
	public HashMap<ImageIdentifier, Set<String>> getUUIDIndex() {
		return UUIDIndex;
	}

	public void printUUIDIndex() {
		System.out.println("StoreDoubleIndex: " + UUIDIndex.size() + " images");
		for(Entry<ImageIdentifier, Set<String>> image: UUIDIndex.entrySet()) {
			System.out.print(image.getKey() + ":");
			for(String store: image.getValue()) {
				System.out.print(" " + store);
			}
			System.out.println();
		}
	}
	
	public String[] getArchiveNames() {
		return archives.toArray(new String[archives.size()]);
	}
	
	public void printDatabasesIndex() {
		System.out.println("MultiArchiveIndex: " + elementsByArchive.size() + " stores");
		for(Entry<Set<String>, Set<ImageIdentifier>> store: elementsByArchive.entrySet()) {
			System.out.print("<");
			for(String storeName: store.getKey()) {
				System.out.print(" " + storeName);	
			}
			System.out.println("> :" + store.getValue().size() + " images");
			
			for(ImageIdentifier image: store.getValue()) {
				System.out.println(image);
			}
			System.out.println();
		}
	}

	public void add(SingleArchiveIndex index) {
		String storeName = index.getArchiveName();
		for(ImageIdentifier imageID: index.getImageIDs()) {
			add(imageID, storeName);
		}
	}

}
