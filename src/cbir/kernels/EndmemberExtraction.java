package cbir.kernels;

import cbir.metadata.Endmember;

public interface EndmemberExtraction {
	
	/**
	 * 
	 * @param image
	 * @param P
	 * @param num_lines
	 * @param num_samples
	 * @param num_bands
	 * @param lines_samples
	 * @return float* Ab
	 */
	Endmember[] exec(float[] image, int[] P, int numBands, int linesSamples,
			int numEndmembers);
}
