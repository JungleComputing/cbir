package cbir.frontend.activities;

import ibis.constellation.Activity;
import ibis.constellation.ActivityContext;
import cbir.frontend.QueryExecutor;
import cbir.kernels.activities.KernelActivity;

/**
 * @author Timo van Kessel
 * 
 */
public abstract class QueryActivity extends Activity {

	protected QueryActivity(ActivityContext context, boolean restrictToLocal,
			boolean willReceiveEvents) {
		super(context, restrictToLocal, willReceiveEvents);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4689955325122447948L;

	@Override
	public QueryExecutor getExecutor() {
		return (QueryExecutor) super.getExecutor();
	}

}
