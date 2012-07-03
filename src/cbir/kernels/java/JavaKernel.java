package cbir.kernels.java;

public class JavaKernel {
	protected static double elapsedTime(long tStart, long tEnd) {
		return ((double) (tEnd - tStart)) / 1000000000;
	}
}
