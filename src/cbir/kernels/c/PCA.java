package cbir.kernels.c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCA extends CKernel implements cbir.kernels.PCA {
	private static final Logger logger = LoggerFactory
			.getLogger(PCA.class);
	
	private static native float[] pca(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples);
	
	@Override
	public float[] exec(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples) {
		long start = System.nanoTime(); 
		float[] result = pca(inputImage, numLines, numSamples, numBands, linesSamples);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("PCA\t%f", elapsedTime(start, System.nanoTime())));
		}
		return result;
	}

}
