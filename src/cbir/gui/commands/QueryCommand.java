package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;
import cbir.backend.MultiArchiveIndex;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.frontend.QueryInitiator;

public class QueryCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519036232400258693L;
	private ImageIdentifier imageID;
	private FloatImage query;
	private final MultiArchiveIndex scope;

	public QueryCommand(ImageIdentifier imageID, MultiArchiveIndex scope) {
		this.imageID = imageID;
		query = null;
		this.scope = scope;
	}

	public QueryCommand(ImageIdentifier imageID) {
		this(imageID, null);
	}

	public QueryCommand(FloatImage query, MultiArchiveIndex scope) {
		this.query = query;
		imageID = query.getID();
		this.scope = scope;
	}

	public QueryCommand(FloatImage query) {
		this(query, null);
	}

	@Override
	public void execute(QueryInitiator qi, ActivityIdentifier destination) {
		if (query == null) {
			qi.query(imageID, scope, destination);
		} else {
			qi.query(query, scope, destination);
		}
	}

	@Override
	public String toString() {
		return String.format("QueryCommand<%s>", imageID);
	}

}
