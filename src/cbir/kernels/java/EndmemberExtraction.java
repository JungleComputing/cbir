package cbir.kernels.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.metadata.Endmember;

public class EndmemberExtraction extends JavaKernel implements cbir.kernels.EndmemberExtraction {
	private static final Logger logger = LoggerFactory
		.getLogger(EndmemberExtraction.class);
	
	@Override
	public Endmember[] exec(float[] image, int[] P, int numBands,
			int linesSamples, int numEndmembers) {
		long start = System.nanoTime();
		Endmember[] endmembers = new Endmember[numEndmembers];
		for (int k = 0; k < numEndmembers; k++) {
			float[] elements = new float[numBands];
			endmembers[k] = new Endmember(elements);
			for (int l = 0; l < numBands; l++) {
				elements[l] = image[l * linesSamples + P[k]];
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("EndMemberExtraction\t%f", elapsedTime(start, System.nanoTime())));
		}
		return endmembers;
	}

}
