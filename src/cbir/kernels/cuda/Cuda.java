package cbir.kernels.cuda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cuda {

    private static final Logger logger = LoggerFactory.getLogger(Cuda.class);

    private static long[] handles;

    static {
        long[] new_handles = null;
        try {
            System.loadLibrary("cbir_cuda");
            new_handles = initCuda();
            logger.info("Cuda initialized!");
        } catch (UnsatisfiedLinkError e) {
            logger.warn("UnsatisfiedLinkError:  " + e.getMessage());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        } finally {
            handles = new_handles;
        }
    }

    /**
     * 
     * @return a handle for each device present in the machine
     * @throws Exception
     */
    private static native long[] initCuda() throws Exception;

    private static native void destroyCuda(long[] handles);

    public static void finish() {
        if (handles != null) {
            destroyCuda(handles);
            logger.info("Cuda destroyed!");
            handles = null;
        }
    }

    public static boolean available() {
        return handles != null;
    }

    public static long[] getHandles() {
        return handles;
    }

    public static long getHandle(int device) throws Exception {
        if (available()) {
            if (device > handles.length) {
                throw new Exception("Invalid device");
            } else {
                return handles[device];
            }
        } else {
            throw new Exception("Cuda not available");
        }

    }

    public static long getNDevices() {
        if (available()) {
            return handles.length;
        } else {
            return 0;
        }
    }

}
