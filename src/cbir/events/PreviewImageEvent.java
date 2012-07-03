package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.envi.PreviewImage;

public class PreviewImageEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8351010688149966539L;

	public PreviewImageEvent(ActivityIdentifier source,
			ActivityIdentifier target, PreviewImage image) {
		super(source, target, image);
	}

	public PreviewImage getImage() {
		return (PreviewImage) super.data;
	}
	
}
