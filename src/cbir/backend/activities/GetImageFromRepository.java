package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.repository.RepositoryExecutor;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.events.FloatImageEvent;

/**
 * @author Timo van Kessel
 * 
 */
public class GetImageFromRepository extends RepositoryActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(GetImageFromRepository.class);
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1671988589204849364L;

	private ImageIdentifier imageID;
	private ActivityIdentifier[] targets;


	public GetImageFromRepository(ImageIdentifier imageID, String[] repositories,
			ActivityIdentifier... targets) {
		super(createContext(true, repositories), false, false);

		this.imageID = imageID;
		this.targets = targets;
	}

	public GetImageFromRepository(ImageIdentifier imageID, String repository,
			ActivityIdentifier... targets) {
		super(createContext(true, repository), false, false);
		this.imageID = imageID;
		this.targets = targets;
	}

	@Override
	public void initialize() throws Exception {
		RepositoryExecutor e = getExecutor();
		FloatImage result = e.getImage(e.getHeader(imageID));
		for(ActivityIdentifier target: targets) {
			e.send(new FloatImageEvent(identifier(), target, result));
		}
		finish();
	}

	@Override
	public void process(Event e) throws Exception {
		//empty
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
