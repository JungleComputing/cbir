package cbir.kernels.cuda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CudaKernel {
	private static boolean available = false;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CudaKernel.class);
	
	static {
		available = true;
		try {
			System.loadLibrary("cbir_cuda");
			initCuda();
			logger.info("Cuda intialized!");	
		} catch (UnsatisfiedLinkError e) {
			logger.warn("UnsatisfiedLinkError:  " + e.getMessage());
			available = false;
		} catch (Exception e) {
			logger.warn(e.getMessage());
			available = false;
		}
	}
	
	private static native void initCuda() throws Exception;
	
	public static boolean available() {
		return available;
	}
	
	protected static double elapsedTime(long tStart, long tEnd) {
		return ((double) (tEnd - tStart)) / 1000000000;
	}
}
