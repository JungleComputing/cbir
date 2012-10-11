package cbir.gui.commands;

import ibis.constellation.ActivityIdentifier;

import java.io.Serializable;

import cbir.frontend.QueryInitiator;

public abstract class Command implements Serializable {

    private final long timestamp;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -622147443204931457L;

	Command() {
	    timestamp = System.nanoTime();
	}
	
	public long getTimeStamp() {
	    return timestamp;
	}
	
	public abstract void execute(QueryInitiator qi, ActivityIdentifier destination);
	

}
