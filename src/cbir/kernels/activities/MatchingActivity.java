package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.MatchTable;
import cbir.metadata.EndmemberSet;

public class MatchingActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;
	
	private static final Logger logger = LoggerFactory
			.getLogger(MatchingActivity.class);
	
	private EndmemberSet endmembers;
	private EndmemberSet referenceEndmembers;
	private ActivityIdentifier[] targets;
	
	public MatchingActivity(EndmemberSet endmembers, EndmemberSet referenceEndmembers, ActivityIdentifier... targets) {
		super(Contexts.matching, false, false);
		this.endmembers = endmembers;
		this.referenceEndmembers = referenceEndmembers;
		this.targets = targets;
	}
	
	@Override
	public void initialize() {
//		long time = -System.nanoTime();
		MatchTable table = getKernels().match(endmembers, referenceEndmembers);
		
		send(table, targets);
		logger.debug("done");
		finish();
//		time += System.nanoTime();
//		System.out.println("MatchingActivity.intitialize() took " + time/1000 + "micros");
	}

	@Override
	public void process(Event e) throws Exception {
//		long time = -System.nanoTime();
		logger.debug("Unexpected event");
		finish();

//		time += System.nanoTime();
//		System.out.println("MatchingActivity.process() took " + time/1000 + "micros");
	}

	@Override
	public void cancel() {

	}

	@Override
	public void cleanup() throws Exception {
		
	}

//	@Override
//	public void cleanup() throws Exception {
//		long time = -System.nanoTime();
//		super.cleanup();
//		time += System.nanoTime();
//		System.out.println("MatchingActivity.cleanup() took " + time/1000 + "micros");
//	}
	
}
