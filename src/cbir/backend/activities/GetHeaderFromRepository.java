package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.repository.RepositoryExecutor;
import cbir.envi.EnviHeader;
import cbir.envi.ImageIdentifier;
import cbir.events.EnviHeaderEvent;

/**
 * @author Timo van Kessel
 * 
 */
public class GetHeaderFromRepository extends RepositoryMasterActivity {

    private static final Logger logger = LoggerFactory
            .getLogger(GetHeaderFromRepository.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -1671988589204849364L;

    private ImageIdentifier imageID;
    private ActivityIdentifier[] targets;
    private String[] repositories;

    public GetHeaderFromRepository(ImageIdentifier imageID,
            String[] repositories, ActivityIdentifier... targets) {
        super(createContext(true, repositories), false, false);

        this.imageID = imageID;
        this.targets = targets;
        this.repositories = repositories.clone();
    }

    public GetHeaderFromRepository(ImageIdentifier imageID, String repository,
            ActivityIdentifier... targets) {
        super(createContext(true, repository), false, false);
        this.imageID = imageID;
        this.targets = targets;
        this.repositories = new String[] {repository};
    }

    @Override
    public void initialize() throws Exception {
        RepositoryExecutor e = getExecutor();
        EnviHeader result = e.getHeader(imageID, repositories);
        for (ActivityIdentifier target : targets) {
            e.send(new EnviHeaderEvent(identifier(), target, result));
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
