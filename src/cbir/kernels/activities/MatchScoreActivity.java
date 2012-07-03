package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.metadata.EndmemberSet;

public class MatchScoreActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;

	private static final Logger logger = LoggerFactory
			.getLogger(MatchScoreActivity.class);

	private EndmemberSet endmembers;
	private EndmemberSet referenceEndmembers;
	private final ActivityIdentifier[] targets;

	public MatchScoreActivity(EndmemberSet endmembers,
			EndmemberSet referenceEndmembers, ActivityIdentifier... targets) {
		super(Contexts.matching, false, false);
		this.endmembers = endmembers;
		this.referenceEndmembers = referenceEndmembers;
		this.targets = targets;
	}

	@Override
	public void initialize() {
		// long time = -System.nanoTime();
		float score = getKernels().matchScore(endmembers, referenceEndmembers);
		send(score, targets);
		logger.debug("done");
		finish();
		// time += System.nanoTime();
		// System.out.println("MatchingActivity.intitialize() took " + time/1000
		// + "micros");
	}

	@Override
	public void process(Event e) throws Exception {
		// long time = -System.nanoTime();
		logger.debug("Unexpected event");
		finish();

		// time += System.nanoTime();
		// System.out.println("MatchingActivity.process() took " + time/1000 +
		// "micros");
	}

	@Override
	public void cancel() {
		// empty
	}

	@Override
	public void cleanup() throws Exception {
		// empty
	}

}
