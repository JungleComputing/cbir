package cbir.backend.activities;

import ibis.constellation.ActivityContext;
import ibis.constellation.context.OrActivityContext;
import ibis.constellation.context.UnitActivityContext;
import cbir.backend.repository.RepositoryExecutor;
import cbir.kernels.activities.KernelActivity;
import cbir.vars.ContextStrings;

public abstract class RepositoryActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3023916684381764734L;

	protected static final ActivityContext createContext(String... repositories) {
		return createContext(UnitActivityContext.DEFAULT_RANK, repositories);
	}
	
	protected static final ActivityContext createContext(long rank, String... repositories) {
		if (repositories == null || repositories.length == 0) {
			throw new IllegalArgumentException("At least one repository needed");
		} else if (repositories.length == 1) {
			return new UnitActivityContext(ContextStrings.createForRepository(repositories[0]), rank);
		} else {
			UnitActivityContext[] contexts = new UnitActivityContext[repositories.length];
			for (int i = 0; i < repositories.length; i++) {
				contexts[i] = new UnitActivityContext(
						ContextStrings.createForRepository(repositories[i]),rank);
			}
			return new OrActivityContext(contexts);
		}
	}

	protected RepositoryActivity(ActivityContext context,
			boolean restrictToLocal, boolean willReceiveEvents) {
		super(context, restrictToLocal, willReceiveEvents);
	}

	@Override
	public RepositoryExecutor getExecutor() {
		return (RepositoryExecutor) super.getExecutor();
	}

}
