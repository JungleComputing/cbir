package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;
import cbir.envi.ImageIdentifier;
import cbir.frontend.QueryInitiator;

public class GetPreviewCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519036232400258693L;
	private ImageIdentifier imageID;
	private String[] stores;
	private int red, green, blue;

	public GetPreviewCommand(ImageIdentifier imageID, int red, int green, int blue,
			String[] stores) {
		this.imageID = imageID;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.stores = stores;
	}

	@Override
	public void execute(QueryInitiator qi, ActivityIdentifier destination) {
		qi.getImagePreview(imageID, red, green, blue, stores, destination);
	}

	@Override
	public String toString() {
		return String.format("GetPreviewCommand<%s>[%d,%d,%d]", imageID, red,
				green, blue);
	}
}
