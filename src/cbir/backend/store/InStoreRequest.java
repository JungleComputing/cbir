package cbir.backend.store;

import cbir.envi.ImageIdentifier;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

/**
 * @author Timo van Kessel
 * 
 */
public class InStoreRequest extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8861324030396936657L;

	/**
	 * @param source
	 * @param target
	 * @param data
	 */
	public InStoreRequest(ActivityIdentifier source, ActivityIdentifier target,
			ImageIdentifier imageID) {
		super(source, target, imageID);
	}

	public ImageIdentifier getImageID() {
		return (ImageIdentifier) data;
	}
}
