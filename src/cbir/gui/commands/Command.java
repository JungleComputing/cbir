package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;

import java.io.Serializable;

import cbir.frontend.QueryInitiator;

public abstract class Command implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -622147443204931457L;

	public abstract void execute(QueryInitiator qi, ActivityIdentifier destination);
	

}
