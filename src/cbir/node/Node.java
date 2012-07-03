package cbir.node;

import ibis.constellation.Activity;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Constellation;
import ibis.constellation.ConstellationFactory;
import ibis.constellation.Event;
import ibis.constellation.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Node {
	private static final Logger logger = LoggerFactory
			.getLogger(Node.class);

	// private Kernels kernels;
	private Constellation constellation;

	protected Node(Executor... executors) {

		logger.debug("init");
		try {
			constellation = ConstellationFactory.createConstellation(executors);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		logger.debug("activate");
	}
	
	public void activate() {
		constellation.activate();
		logger.debug("start");
	}

	public ActivityIdentifier submit(Activity activity) {
		return constellation.submit(activity);
		
	}
	
	public void send(Event e) {
		constellation.send(e);
	}

	public void done() {
		constellation.done();
	}
	
//	private boolean termination = false;
//	
//	public synchronized void terminate() {
//		termination  = true;
//		notifyAll();
//	}
//	
//	public synchronized void waitForTermination() {
//		while(!termination) {
//			try {
//				wait();
//			} catch (InterruptedException e) {
//			}
//		}
//	}

}
