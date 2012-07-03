package cbir.node;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.Cbir;
import cbir.frontend.ControlExecutor;
import cbir.gui.CommandActivity;
import cbir.gui.ControlActivity;
import cbir.gui.Controller;
import cbir.gui.commands.Command;

/**
 * @author Timo van Kessel
 * 
 */
public class ControlNode extends Node {
	
	private Controller controller;
	private ActivityIdentifier controlID;
	private ActivityIdentifier commandID;
	
	public static ControlNode createControlNode() {
		System.out.println("Initializing Cbir");
		Cbir cbir = new Cbir();
		System.out.println("Initializing ControlExecutor");
		ControlExecutor ge = cbir.getFactory().createControlExecutor();
		System.out.println("Creating GUINode");
		return new ControlNode(ge);
	}

	private ControlNode(ControlExecutor executor) {
		super(executor);
	}
	
	public void activate(Controller controller) {
		this.controller = controller;
		activate();
		controlID = submit(new ControlActivity(controller));
		commandID = submit(new CommandActivity());
	}

	public Controller getController() {
		return controller;
	}
	
	public void submit(Command command) {
		send(new Event(controlID, commandID, command));
	}
}
