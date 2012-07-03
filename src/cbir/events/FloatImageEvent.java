package cbir.events;

import cbir.envi.FloatImage;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class FloatImageEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4562838101945769889L;

	public FloatImageEvent(ActivityIdentifier source,
			ActivityIdentifier target, FloatImage image) {
		super(source, target, image);
	}

	public FloatImage getImage() {
		return (FloatImage) super.data;
	}
	
}
