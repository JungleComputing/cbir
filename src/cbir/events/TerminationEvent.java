package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class TerminationEvent extends Event {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9137394393608874399L;

	public TerminationEvent(ActivityIdentifier source,
			ActivityIdentifier target) {
		super(source, target, null);
	}

	
}
