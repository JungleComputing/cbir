package cbir.frontend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.backend.activities.StoreActivity;
import cbir.backend.store.MetadataMessage;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Metadata;

public class GetMetaDataFromStore extends StoreActivity {

	private static final long serialVersionUID = -3375885765826899357L;

	private ImageIdentifier imageID;
	private ActivityIdentifier destination;

	public GetMetaDataFromStore(ImageIdentifier imageID, String[] stores,
			ActivityIdentifier destination) {
		super(false, false, stores);
		this.destination = destination;
		this.imageID = imageID;
	}

	@Override
	public void initialize() throws Exception {
		Metadata md = getStore().get(imageID);
		if (md == null) {
			System.out.println("Metadata for " + imageID + " == null!!");
			throw new Error("Metadata for " + imageID + " == null!!");
		}
		getExecutor().send(new MetadataMessage(identifier(), destination, md));
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
