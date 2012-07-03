package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.repository.RepositoryExecutor;
import cbir.envi.EnviHeader;
import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel
 * 
 */
public class CreateTilesAndGetFeatureVectors extends RepositoryActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(CreateTilesAndGetFeatureVectors.class);

	private final ImageIdentifier imageID;
	private final ActivityIdentifier[] targets;
	private final String[] repositories;
	private final int tileWidth;
	private final int tileHeight;

	public CreateTilesAndGetFeatureVectors(ImageIdentifier imageID, int tileWidth, int tileHeight, String[] repositories,
			ActivityIdentifier... targets) {
		super(createContext(repositories), false, true);
		this.repositories = repositories;
		this.imageID = imageID;
		this.targets = targets;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public CreateTilesAndGetFeatureVectors(ImageIdentifier imageID, int tileWidth, int tileHeight, String repository,
			ActivityIdentifier... targets) {
		super(createContext(repository), false, true);

		this.imageID = imageID;
		this.targets = targets;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		repositories = new String[] {repository};
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1671988589204849364L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		RepositoryExecutor e = getExecutor();
		
		EnviHeader header = e.getHeader(imageID);
		EnviHeader[] tiles = header.createTiles(tileWidth, tileHeight);
		for(EnviHeader tile: tiles) {
			e.submit(new GenerateFeatureVectors(tile, repositories, targets));
		}
		finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#process(ibis.constellation.Event)
	 */
	@Override
	public void process(Event e) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#cleanup()
	 */
	@Override
	public void cleanup() throws Exception {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#cancel()
	 */
	@Override
	public void cancel() throws Exception {
		// empty
	}

}
