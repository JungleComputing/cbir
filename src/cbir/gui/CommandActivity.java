package cbir.gui;

import ibis.constellation.Activity;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.frontend.QueryInitiator;
import cbir.gui.commands.Command;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

/**
 * @author Timo van Kessel
 *
 */
public class CommandActivity extends Activity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2961318647144846876L;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CommandActivity.class);

	/**
	 */
	public CommandActivity() {
		super(new CBIRActivityContext(ContextStrings.QUERY_INITIATOR, true), false, true);
	}
	
	@Override
	public QueryInitiator getExecutor() {
		return (QueryInitiator) super.getExecutor();
	}

	@Override
	public void initialize() throws Exception {
		System.out.println("CommandActivity started, ID = " + identifier());
		suspend();
	}

	@Override
	public void process(Event e) throws Exception {
		if(e.data instanceof Command) {
			System.out.println("Received a command event: " + e.data);
			logger.debug("Received a command event: " + e.data);
			((Command)e.data).execute(getExecutor(), e.source);
		} else {
			System.out.println("Received an unknown event: " + e.data);
			logger.debug("Received an unknown event: " + e.data);
		}
		System.out.println("Command processed, waiting for next");
		suspend();
	}

	@Override
	public void cleanup() throws Exception {
		System.out.println("CommandActivity finished!, ID = " + identifier());
	}

	@Override
	public void cancel() throws Exception {
	}

}
