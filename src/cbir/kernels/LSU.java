package cbir.kernels;

public interface LSU {
	
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
	float[] exec(float[] image, int[] P, int num_lines, int num_samples,
			int num_bands, int lines_samples, int num_endmembers);
}
