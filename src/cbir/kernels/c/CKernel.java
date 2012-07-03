package cbir.kernels.c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CKernel {
	private static boolean available = false;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CKernel.class);
	
	static {
		available = true;
		try {
			System.loadLibrary("java_cbir");
		} catch (UnsatisfiedLinkError e) {
			logger.warn(e.getMessage());
			available = false;
		}		
	}
	
	public static boolean available() {
		return available;
	}
	
	protected static double elapsedTime(long tStart, long tEnd) {
		return ((double) (tEnd - tStart)) / 1000000000;
	}
}
