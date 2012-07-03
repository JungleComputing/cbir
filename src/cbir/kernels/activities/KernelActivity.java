package cbir.kernels.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.kernels.KernelExecutor;
import cbir.kernels.Kernels;

public abstract class KernelActivity extends Activity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3023916684381764734L;

	protected KernelActivity(ActivityContext context, boolean restrictToLocal,
			boolean willReceiveEvents) {
		super(context, restrictToLocal, willReceiveEvents);
	}

	@Override
	public KernelExecutor getExecutor() {
		return (KernelExecutor) super.getExecutor();
	}

	protected Kernels getKernels() {
		return getExecutor().getKernels();
	}

	protected <T> void send(T payload, ActivityIdentifier... targets) {
		if (targets != null) {
			for (ActivityIdentifier target : targets) {
				getExecutor().send(new Event(identifier(), target, payload));
			}
		}
	}

}
