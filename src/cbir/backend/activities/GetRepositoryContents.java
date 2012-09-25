package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

/**
 * @author Timo van Kessel
 * 
 */
public class GetRepositoryContents extends RepositoryMasterActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(GetRepositoryContents.class);
	
	private final ActivityIdentifier[] targets;

	public GetRepositoryContents(String repositoryName,
			ActivityIdentifier... targets) {
		super(new CBIRActivityContext(
				ContextStrings.createForRepositoryMaster(repositoryName), true),
				false, false);
		this.targets = targets;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -485506053464244490L;

	@Override
	public void initialize() throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("delivering contents...");
		}
		send(getExecutor().getIndex(), targets);
		if(logger.isDebugEnabled()) {
			logger.debug("...done");
		}
		finish();
	}

	@Override
	public void process(Event e) throws Exception {
		// empty
	}

	@Override
	public void cleanup() throws Exception {
		// empty
	}

	@Override
	public void cancel() throws Exception {
		// empty
	}

}
