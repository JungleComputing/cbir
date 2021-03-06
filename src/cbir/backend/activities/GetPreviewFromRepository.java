package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel
 * 
 */
public class GetPreviewFromRepository extends RepositoryMasterActivity {
    // TODO convert this one into a batch operation?

    private static final Logger logger = LoggerFactory
            .getLogger(GetPreviewFromRepository.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -1671988589204849364L;

    private final ActivityIdentifier[] targets;
    private final ImageIdentifier imageID;
    private final int red, green, blue;
    private final String[] repositories;

    public GetPreviewFromRepository(ImageIdentifier imageID, int red,
            int green, int blue, String[] repositories,
            ActivityIdentifier... targets) {
        super(createContext(true, repositories), false, false);

        this.imageID = imageID;
        this.targets = targets;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.repositories = repositories;
    }

    public GetPreviewFromRepository(ImageIdentifier imageID, int red,
            int green, int blue, String repository,
            ActivityIdentifier... targets) {
        super(createContext(true, repository), false, false);
        this.imageID = imageID;
        this.targets = targets;
        this.red = red;
        this.green = green;
        this.blue = blue;
        repositories = new String[] { repository };
    }

    @Override
    public void initialize() throws Exception {
        getExecutor().submit(
                new LoadPreviewActivity(imageID, red, green, blue,
                        repositories, targets));
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
