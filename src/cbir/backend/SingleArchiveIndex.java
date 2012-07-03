package cbir.backend;

import java.io.Serializable;
import java.util.Arrays;

import cbir.envi.ImageIdentifier;

public class SingleArchiveIndex implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6909393836689613532L;
	
	private final String archiveName;
	private final ImageIdentifier[] imageIDs;
	
	public SingleArchiveIndex(String archiveName, ImageIdentifier[] imageIDs) {
		if(imageIDs == null) {
			imageIDs = new ImageIdentifier[0];
		}
		this.archiveName = archiveName;
		this.imageIDs = imageIDs;
		
	}
	
	public SingleArchiveIndex(SingleArchiveIndex original) {
		this(original.archiveName, Arrays.copyOf(original.imageIDs, original.imageIDs.length));
	}

	public String getArchiveName() {
		return archiveName;
	}
	
	public ImageIdentifier[] getImageIDs() {
		return imageIDs;
	}
	
	public int size() {
		return imageIDs.length;
	}

}
