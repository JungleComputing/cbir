package cbir.kernels.cuda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CudaKernel {

    private static final Logger logger = LoggerFactory
            .getLogger(CudaKernel.class);

    private final long handle;
    
    protected CudaKernel(long handle) {
        this.handle = handle;
    }
    
    protected long getHandle() {
        return handle;
    }
    
    protected double elapsedTime(long tStart, long tEnd) {
        return ((double) (tEnd - tStart)) / 1000000000;
    }
}
