package cbir.kernels.c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NFindr extends CKernel implements cbir.kernels.NFindr {
	private static final Logger logger = LoggerFactory
			.getLogger(NFindr.class);
	
	private static native int[] nFindr(float[] inputImage, int numSamples,
			int numPrincipalComponents, int linesSamples, boolean generateRandomValues,
			String nfinderInitFile);
	
	@Override
	public int[] exec(float[] image, int numSamples,
			int numPrincipalComponents, int linesSamples, boolean generateRandomValues,
			String nfinderInitFile) {
		long start = System.nanoTime();
		int[] result = nFindr(image, numSamples, numPrincipalComponents, linesSamples, generateRandomValues, nfinderInitFile);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("NFINDR\t%f", elapsedTime(start, System.nanoTime())));
		}
		return result;
	}
}
