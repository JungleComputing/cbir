package cbir.backend.store;

import cbir.envi.ImageIdentifier;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class InStoreMessage extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7302652528222812158L;

	private final boolean inStore;

	public InStoreMessage(ActivityIdentifier source, ActivityIdentifier target,
			ImageIdentifier imageID, boolean inStore) {
		super(source, target, imageID);
		this.inStore = inStore;
	}

	public ImageIdentifier getImageID() {
		return (ImageIdentifier) data;
	}

	public boolean inStore() {
		return inStore;
	}
}
