package cbir.kernels;


public interface NFindr {

	/**
	 * 
	 * @param _image
	 * @param num_samples
	 * @param num_principal_components
	 * @param lines_samples
	 * @param g_aleatorios
	 * @param nfinder_init_file
	 * @return int*P
	 */
	int[] exec(float[] _image, int num_samples, int num_principal_components,
			int lines_samples, boolean generateRandomValues,
			String nfinder_init_file);
}
