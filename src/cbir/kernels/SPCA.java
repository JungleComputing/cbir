package cbir.kernels;


public interface SPCA {
	
	public float[] exec(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples, int numPrincipalComponents,
			boolean generate, String randomVectorFile, int numIterations);
}
