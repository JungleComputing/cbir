package cbir.backend.store;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class StoreIndexRequest extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8001689113973930205L;

	public StoreIndexRequest(ActivityIdentifier source,
			ActivityIdentifier target) {
		super(source, target, null);
	}
}
