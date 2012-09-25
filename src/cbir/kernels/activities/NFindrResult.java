package cbir.kernels.activities;


public class NFindrResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4562838101945769889L;

	private int[] results;
	
	public NFindrResult(int[] data) {
		this.results = data;
	}

	public int[] getResults() {
		return results;
	}
	
}
