package cbir.kernels.c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPCA extends CKernel implements cbir.kernels.SPCA {
	private static final Logger logger = LoggerFactory
			.getLogger(SPCA.class);
	
	private static native float[] spca(float[] inputImage, int numLines,
			int numSamples, int numBands, int linesSamples,
			int numPrincipalComponents, boolean generate, String randomVectorFile,
			int numIterations);
	
	@Override
	public float[] exec(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples, int numPrincipalComponents,
			boolean generate, String randomVectorFile, int numIterations) {
//		if(logger.isDebugEnabled()) {
//			logger.debug(String.format("SPCA.exec(): %d lines, %d samples, %d bands, %d linessamples, %d pc's", numLines, numSamples, numBands, linesSamples, numPrincipalComponents));
//		}
		
		long start = System.nanoTime();
		float[] result = spca(inputImage, numLines, numSamples, numBands,
				linesSamples, numPrincipalComponents, generate,
				randomVectorFile, numIterations);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("SPCA_C\t%f",
				elapsedTime(start, System.nanoTime())));
		}
		return result;
	}

}
