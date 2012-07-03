package cbir.backend.store;

import cbir.backend.SingleArchiveIndex;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

/**
 * @author Timo van Kessel
 * 
 */
public class StoreIndexMessage extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1414861146516501448L;

	/**
	 * @param source
	 * @param target
	 * @param data
	 */
	public StoreIndexMessage(ActivityIdentifier source,
			ActivityIdentifier target, SingleArchiveIndex index) {
		super(source, target, index);

	}

	public SingleArchiveIndex getIndex() {
		return (SingleArchiveIndex) data;
	}

}
