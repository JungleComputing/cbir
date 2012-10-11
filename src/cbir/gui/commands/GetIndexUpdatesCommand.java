package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;
import cbir.frontend.QueryInitiator;

public class GetIndexUpdatesCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519036232400258693L;

	@Override
	public void execute(QueryInitiator qi, ActivityIdentifier destination) {
		qi.registerForIndexUpdates(destination);
	}

	@Override
	public String toString() {
		return "GetIndexUpdatesCommand";
	}
}
