package cbir.backend.store;

import cbir.envi.ImageIdentifier;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class MetadataRequest extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8001689113973930205L;

	public MetadataRequest(ActivityIdentifier source,
			ActivityIdentifier target, ImageIdentifier imageID) {
		super(source, target, imageID);
	}
	
	public ImageIdentifier getImageID() {
		return (ImageIdentifier)(data);
	}

}
