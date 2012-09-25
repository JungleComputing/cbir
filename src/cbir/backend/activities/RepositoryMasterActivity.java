package cbir.backend.activities;

import ibis.constellation.ActivityContext;
import ibis.constellation.context.OrActivityContext;
import ibis.constellation.context.UnitActivityContext;
import cbir.CBIRActivity;
import cbir.backend.repository.RepositoryMasterExecutor;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

public abstract class RepositoryMasterActivity extends CBIRActivity {
	
	protected static ActivityContext createContext(boolean interactive, String... repositories) {
		if (repositories == null || repositories.length == 0) {
			throw new IllegalArgumentException("At least one repository needed");
		} else if (repositories.length == 1) {
			return new CBIRActivityContext(ContextStrings.createForRepositoryMaster(repositories[0]), interactive);
		} else {
			UnitActivityContext[] contexts = new UnitActivityContext[repositories.length];
			for (int i = 0; i < repositories.length; i++) {
				contexts[i] = new CBIRActivityContext(
						ContextStrings.createForRepository(repositories[i]), interactive);
			}
			return new OrActivityContext(contexts);
		}
	}

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
