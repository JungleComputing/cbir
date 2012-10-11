package cbir.kernels.cuda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LSU extends CudaKernel implements cbir.kernels.LSU {

    private static final Logger logger = LoggerFactory.getLogger(LSU.class);

    public LSU(long handle) {
        super(handle);
    }

    private static native float[] lsu(float[] image, int[] P, int numLines,
            int numSamples, int numBands, int linesSamples, int numEndmembers,
            long handle);

    @Override
    public float[] exec(float[] image, int[] P, int numLines, int numSamples,
            int numBands, int linesSamples, int numEndmembers) {
        long start = System.nanoTime();
        float[] result = lsu(image, P, numLines, numSamples, numBands,
                linesSamples, numEndmembers, getHandle());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("LSU_Cuda\t%f",
                    elapsedTime(start, System.nanoTime())));
        }

        return result;
    }
}
