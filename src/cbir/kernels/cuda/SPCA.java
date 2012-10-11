package cbir.kernels.cuda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPCA extends CudaKernel implements cbir.kernels.SPCA {

    private static final Logger logger = LoggerFactory.getLogger(SPCA.class);

    private static native float[] spca(float[] inputImage, int numLines,
            int numSamples, int numBands, int linesSamples,
            int numPrincipalComponents, boolean generate,
            String randomVectorFile, boolean fixedNumIterations,
            int numIterations, long handle);

    public SPCA(long handle) {
        super(handle);
    }

    @Override
    public float[] exec(float[] inputImage, int numLines, int numSamples,
            int numBands, int linesSamples, int numPrincipalComponents,
            boolean generate, String randomVectorFile,
            boolean fixedNumIterations, int numIterations) {
        long start = System.nanoTime();
        float[] result = spca(inputImage, numLines, numSamples, numBands,
                linesSamples, numPrincipalComponents, generate,
                randomVectorFile, fixedNumIterations, numIterations,
                getHandle());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("SPCA_cuda\t%f",
                    elapsedTime(start, System.nanoTime())));
        }
        return result;
    }

}
