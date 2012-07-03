package cbir.kernels;


public interface PCA {
	
	/**
	 * 
	 * @param input_image
	 * @param num_lines
	 * @param num_samples
	 * @param num_bands
	 * @param lines_samples
	 * @param iterations
	 * @return float* h_result
	 */
	public float[] exec(float[] input_image, int num_lines, int num_samples,
			int num_bands, int lines_samples);
}
