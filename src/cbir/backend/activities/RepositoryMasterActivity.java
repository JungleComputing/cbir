package cbir.backend.activities;

import ibis.constellation.ActivityContext;
import cbir.backend.repository.RepositoryMasterExecutor;

public abstract class RepositoryMasterActivity extends RepositoryActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3023916684381764734L;

	protected RepositoryMasterActivity(ActivityContext context, boolean restrictToLocal,
			boolean willReceiveEvents) {
		super(context, restrictToLocal, willReceiveEvents);
	}

	@Override
	public RepositoryMasterExecutor getExecutor() {
		return (RepositoryMasterExecutor) executor;
	}

}
