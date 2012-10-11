package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.backend.SingleArchiveIndex;

public class StoreIndexUpdateEvent extends Event {


	/**
	 * 
	 */
	private static final long serialVersionUID = 49225409224367393L;

	public StoreIndexUpdateEvent(ActivityIdentifier source,
			ActivityIdentifier target, SingleArchiveIndex index) {
		super(source, target, index);
	}

	public SingleArchiveIndex getIndex() {
		return (SingleArchiveIndex) super.data;
	}
	
}
