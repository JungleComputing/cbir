package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.repository.RepositoryExecutor;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.events.PreviewImageEvent;

/**
 * @author Timo van Kessel
 * 
 */
public class LoadPreviewActivity extends RepositoryActivity {

    private static final Logger logger = LoggerFactory
            .getLogger(LoadPreviewActivity.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -1671988589204849364L;

    private final ActivityIdentifier[] targets;
    private final ImageIdentifier imageID;
    private final int red, green, blue;

    private String[] repositories;

    public LoadPreviewActivity(ImageIdentifier imageID, int red, int green,
            int blue, String[] repositories, ActivityIdentifier... targets) {
        super(createContext(true, repositories), false, false);

        this.imageID = imageID;
        this.targets = targets;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.repositories = repositories.clone();
    }

    public LoadPreviewActivity(ImageIdentifier imageID, int red, int green,
            int blue, String repository, ActivityIdentifier... targets) {
        super(createContext(true, repository), false, false);
        this.imageID = imageID;
        this.targets = targets;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.repositories = new String[] { repository };
    }

    @Override
    public void initialize() throws Exception {
        RepositoryExecutor e = getExecutor();

        PreviewImage result = e.getPreview(e.getHeader(imageID, repositories), red, green,
                blue, repositories);
        for (ActivityIdentifier target : targets) {
            e.send(new PreviewImageEvent(identifier(), target, result));
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
