package cbir;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

/**
 * @author Timo van Kessel
 * 
 */
public abstract class CBIRActivity extends Activity {

	protected CBIRActivity(ActivityContext context, boolean willReceiveEvents) {
		super(context, willReceiveEvents);
	}

	protected CBIRActivity(ActivityContext context, boolean restrictToLocal,
			boolean willReceiveEvents) {
		super(context, restrictToLocal, willReceiveEvents);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3472437387943271896L;

	protected final <T> void send(T payload, ActivityIdentifier... targets) {
		if (targets != null) {
			for (ActivityIdentifier target : targets) {
				getExecutor().send(new Event(identifier(), target, payload));
			}
		}
	}

}
