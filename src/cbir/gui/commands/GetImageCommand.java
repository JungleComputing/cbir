package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;
import cbir.envi.ImageIdentifier;
import cbir.frontend.QueryInitiator;

public class GetImageCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519036232400258693L;
	private ImageIdentifier imageID;
	private String[] stores;

	public GetImageCommand(ImageIdentifier imageID, String[] stores) {
		this.imageID = imageID;
		this.stores = stores;
	}
	
	public GetImageCommand(ImageIdentifier imageID) {
		this.imageID = imageID;
		this.stores = null;
	}
	
	@Override
	public void execute(QueryInitiator qi, ActivityIdentifier destination) {
		if(stores == null) {
			qi.getImage(imageID, destination);
		}
		qi.getImage(imageID, stores, destination);
	}
	
	@Override
	public String toString() {
		return String.format("GetImageCommand<%s>", imageID);
	}

}
