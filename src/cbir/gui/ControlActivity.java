package cbir.gui;

import ibis.constellation.Activity;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.MatchTable;
import cbir.backend.MultiArchiveIndex;
import cbir.events.EnviHeaderEvent;
import cbir.events.FloatImageEvent;
import cbir.events.PreviewImageEvent;
import cbir.events.QueryResultEvent;
import cbir.events.StoreIndexEvent;
import cbir.events.TerminationEvent;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

/**
 * @author Timo van Kessel
 * 
 */
public class ControlActivity extends Activity {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1802200456983816153L;

    private static final Logger logger = LoggerFactory
            .getLogger(ControlActivity.class);

    private Controller controller;

    public ControlActivity(Controller controller) {
        super(new CBIRActivityContext(ContextStrings.GUI, true), true, true);
        this.controller = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#initialize()
     */
    @Override
    public void initialize() throws Exception {
        System.out.println("ControlActivity started, ID = " + identifier());
        suspend();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#process(ibis.constellation.Event)
     */
    @Override
    public void process(Event e) throws Exception {
        if (e instanceof StoreIndexEvent) {
            System.out.println("Received a StoreIndexEvent, #elements: "
                    + ((MultiArchiveIndex) e.data).size());
            logger.debug("Received a StoreIndexEvent: " + e.data);
            controller.deliverIndex(((StoreIndexEvent) e).getIndex());
            suspend();
        } else if (e instanceof PreviewImageEvent) {
            System.out.println("Received a PreviewImageEvent: " + e.data);
            logger.debug("Received a PreviewImageEvent: " + e.data);
            controller.deliverPreview(((PreviewImageEvent) e).getImage());
            suspend();
        } else if (e instanceof QueryResultEvent) {
            System.out.println("Received a QueryResultEvent, #elements: "
                    + ((MatchTable[]) e.data).length);
            logger.debug("Received a QueryResultEvent, #elements: "
                    + ((MatchTable[]) e.data).length);
            controller.deliverResult(((QueryResultEvent) e).getResults());
            suspend();
        } else if (e instanceof EnviHeaderEvent) {
            System.out.println("Received a EnviHeaderEvent: " + e.data);
            logger.debug("Received a EnviHeaderEvent: " + e.data);
            controller.deliverHeader(((EnviHeaderEvent) e).getHeader());
            suspend();
        } else if (e instanceof FloatImageEvent) {
            System.out.println("Received a FloatImageEvent: " + e.data);
            logger.debug("Received a FloatImageEvent: " + e.data);
            controller.deliverImage(((FloatImageEvent) e).getImage());
            suspend();
        } else if (e instanceof TerminationEvent) {
            System.out.println("Received a TerminationEvent");
            logger.debug("Received a TerminationEvent");
            // FIXME stop other parts of the system as well??;
            // FIXME we should not receive this over here, but at the command
            // node?
            finish();
        } else {
            logger.debug("Unknown event: " + e.data);
            System.out.println("Unknown event: " + e.data);
            suspend();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#cleanup()
     */
    @Override
    public void cleanup() throws Exception {
        System.out.println("ControlActivity FINISHED!");
        System.err.println("ControlActivity FINISHED!");

    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#cancel()
     */
    @Override
    public void cancel() throws Exception {
        // TODO Auto-generated method stub

    }

}
