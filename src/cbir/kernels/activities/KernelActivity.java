package cbir.kernels.activities;

import ibis.constellation.ActivityContext;
import cbir.CBIRActivity;
import cbir.kernels.KernelExecutor;
import cbir.kernels.Kernels;

public abstract class KernelActivity extends CBIRActivity {

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

}
