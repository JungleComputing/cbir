package cbir.metadata;

import java.io.Serializable;

import cbir.envi.ImageIdentifier;

public class EndmemberSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4208688067633187428L;
	private final Endmember[] endmembers;
	private final ImageIdentifier imageID;
	
	public EndmemberSet(ImageIdentifier imageID, Endmember[] endmembers) {
		this.imageID = imageID;
		this.endmembers = endmembers;
	}
	
	public Endmember[] getEndmembers() {
		return endmembers;
	}
	
	public ImageIdentifier getImageID() {
		return imageID;
	}

	public int size() {
		if(endmembers == null) {
			return 0;
		}
		return endmembers.length;
	}

	public int bands() {
		if(endmembers == null) {
			return 0;
		}
		return endmembers[0].bands();
	}
	
	public void print() {
		System.out.println("Endmember '" + getImageID() + "':");
		System.out.println("Size: " + size());
		System.out.println("Bands: " + bands());
	}

}
