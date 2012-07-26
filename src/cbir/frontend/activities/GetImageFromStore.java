package cbir.frontend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.backend.activities.GetImageFromRepository;
import cbir.backend.activities.StoreActivity;
import cbir.envi.ImageIdentifier;

public class GetImageFromStore extends StoreActivity {

	private static final long serialVersionUID = -3375885765826899357L;

	private ImageIdentifier imageID;
	private ActivityIdentifier destination;
	
	public GetImageFromStore(ImageIdentifier imageID, String[] stores, ActivityIdentifier destination) {
		super(true, false, false, stores);
		this.destination = destination;
		this.imageID = imageID;
	}

	@Override
	public void initialize() throws Exception {
		String[] repositories = getStore().getRepositoriesFor(imageID);
		getExecutor().submit(new GetImageFromRepository(imageID, repositories, destination));
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
