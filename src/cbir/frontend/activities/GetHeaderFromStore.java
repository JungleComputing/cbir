package cbir.frontend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.backend.activities.StoreActivity;
import cbir.envi.EnviHeader;
import cbir.envi.ImageIdentifier;
import cbir.events.EnviHeaderEvent;

public class GetHeaderFromStore extends StoreActivity {

	private static final long serialVersionUID = -3375885765826899357L;

	private ImageIdentifier imageID;
	private ActivityIdentifier destination;

	public GetHeaderFromStore(ImageIdentifier imageID, String[] stores,
			ActivityIdentifier destination) {
		super(true, false, false, stores);
		this.destination = destination;
		this.imageID = imageID;
	}

	@Override
	public void initialize() throws Exception {
		EnviHeader header = getStore().get(imageID).getHeader();
		if (header == null) {
			System.out.println("Header for " + imageID + " == null!!");
			throw new Error("Header for " + imageID + " == null!!");
		}
		getExecutor().send(new EnviHeaderEvent(identifier(), destination, header));
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
