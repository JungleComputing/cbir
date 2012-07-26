package cbir.frontend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.backend.activities.GetPreviewFromRepository;
import cbir.backend.activities.StoreActivity;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

public class GetPreviewFromStore extends StoreActivity {

	private static final long serialVersionUID = -3375885765826899357L;

	private ActivityIdentifier destination;
	
	private ImageIdentifier imageID;
	private final int red, green, blue;
	
	
	public GetPreviewFromStore(ImageIdentifier imageID, int red, int green, int blue, String[] stores, ActivityIdentifier destination) {
		super(true, false, false, stores);
		this.destination = destination;
		this.imageID = imageID;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public void initialize() throws Exception {
		String[] repositories = getStore().getRepositoriesFor(imageID);
		getExecutor().submit(new GetPreviewFromRepository(imageID, red, green, blue, repositories, destination));
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
