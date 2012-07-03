package cbir.frontend;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Executor;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

/**
 * @author Timo van Kessel
 *
 */
public interface MetadataCache {

	/**
	 * Gets the metadata of image @param UUID from the cache,
	 * @param imageID
	 * @return the metadata, or NULL when not available
	 */
	Metadata getFromCache(ImageIdentifier imageID);

	boolean inCache(ImageIdentifier imageID);

	ImageIdentifier[] cacheContents();

	/**
	 * Retrieves the metadata of Image from the cache or store and delivers it to target through an event
	 * @param imageID The identifier of the image the metadata is requested for
	 * @param target The Activity the reply will be sent to 
	 */
	void get(ImageIdentifier imageID, ActivityIdentifier target);
	
	/**
	 * 
	 * @param imageID
	 * @param target The activity the reply needs to be sent to
	 */
	void inStore(ImageIdentifier imageID, ActivityIdentifier target);

	String storeName();

	Executor getExecutor();

	void init();
}
