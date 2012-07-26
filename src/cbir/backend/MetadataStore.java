package cbir.backend;

import java.util.Set;

import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

public interface MetadataStore {
	String getName();
	Metadata get(ImageIdentifier imageID);
	String[] getRepositoriesFor(ImageIdentifier imageID);
	boolean contains(ImageIdentifier imageID);
	SingleArchiveIndex contents();
	void put(Metadata metadata, Set<String> originalLocations);
	void put(Metadata metadata, String... originalLocations);
	int size();
	
}
