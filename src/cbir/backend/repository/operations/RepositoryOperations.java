package cbir.backend.repository.operations;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;

public abstract class RepositoryOperations {

    private final String repositoryName;

    protected RepositoryOperations(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    private boolean termination = false;
    
    // statistics
    private long readHeaderOps = 0;
    private long readHeaderTime = 0;
    private long loadDataOps = 0;
    private long loadDataTime = 0;
    private long createBufferedImageOps = 0;
    private long createBufferedImageTime = 0;
    private long contentsTime = 0;
    private long contentsOps = 0;

    public String getRepositoryName() {
        return repositoryName;
    }

    protected abstract EnviHeader doReadHeader(String uuid) throws IOException;
    
    public final EnviHeader readHeader(String uuid) throws IOException {
        long nanos = -System.nanoTime();
        try {
            return doReadHeader(uuid);
        } finally {
            nanos += System.nanoTime();
            readHeaderOps++;
            readHeaderTime += nanos;
        }
    }

    public long readHeaderOps() {
        return readHeaderOps;
    }

    public long readHeaderTime() {
        return readHeaderTime;
    }

    public long readHeaderAvgTime() {
        return readHeaderTime == 0 ? 0 : readHeaderTime/readHeaderOps; 
    }
   

    // public abstract String getDataLocation(String imageName);

    protected abstract FloatImage doLoadData(EnviHeader header)
            throws IOException;

    /**
     * 
     * @param header
     *            The header corresponding to the image file
     * @return The image data
     * @throws IOException
     */
    public final FloatImage loadData(EnviHeader header) throws IOException {
        long nanos = -System.nanoTime();
        try {
            return doLoadData(header);
        } finally {
            nanos += System.nanoTime();
            loadDataOps++;
            loadDataTime += nanos;
        }
    }

    public long loadDataOps() {
        return loadDataOps;
    }

    public long loadDataTime() {
        return loadDataTime;
    }

    public long loadDataAvgTime() {
        return loadDataTime == 0 ? 0 : loadDataTime/loadDataOps; 
    }

    
    protected abstract BufferedImage doCreateBufferedImage(EnviHeader header,
            String uuid, int red, int green, int blue)
            throws UnsupportedOperationException, IOException;
    
    /**
     * 
     * @param header
     *            The header corresponding to the image file
     * @param uuid
     *            UUID of the data file
     * @param the
     *            band that will be used for the red component
     * @param the
     *            band that will be used for the green component
     * @param the
     *            band that will be used for the blue component
     * @return The image data
     * @throws IOException
     */
    public final BufferedImage createBufferedImage(EnviHeader header,
            String uuid, int red, int green, int blue)
            throws UnsupportedOperationException, IOException {
        long nanos = -System.nanoTime();
        try {
        return doCreateBufferedImage(header, uuid, red, green, blue);
        } finally {
            nanos += System.nanoTime();
            createBufferedImageOps++;
            createBufferedImageTime += nanos;
        }
    }

    public long createBufferedImageOps() {
        return createBufferedImageOps;
    }

    public long createBufferedImageTime() {
        return createBufferedImageTime;
    }

    public long createBufferedImageAvgTime() {
        return createBufferedImageTime == 0 ? 0 : createBufferedImageTime/createBufferedImageOps; 
    }

    // public abstract void writeFloatImage(float[] image, String uuid, int
    // numLines,
    // int numSamples, int numBands);

    // public abstract boolean hasEndmembers(String uuid);
    //
    // public abstract EndmemberSet readEndmembers(String uuid) throws
    // IOException;

    // public ActivityIdentifier getActivityID(Executor executor) {
    // if (cacheActivity == null) {
    // cacheActivity = new CacheActivity(name);
    // return executor.submit(cacheActivity);
    // } else {
    // return cacheActivity.identifier();
    // }
    // }

    public synchronized void terminate() {
        termination = true;
        notifyAll();
    }

    public synchronized void waitForTermination() {
        while (!termination) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    protected abstract ImageIdentifier[] doContents() throws IOException;
    
    public final ImageIdentifier[] contents() throws IOException {
        long nanos = -System.nanoTime();
        try {
        return doContents();
        } finally {
            nanos += System.nanoTime();
            contentsOps++;
            contentsTime += nanos;
        }
    }

    public long contentsOps() {
        return contentsOps;
    }

    public long contentsTime() {
        return contentsTime;
    }

    public long contentsAvgTime() {
        return contentsTime == 0 ? 0 : contentsTime/contentsOps; 
    }

    protected String[] stripExtension(String[] files) {
        for (int i = 0; i < files.length; i++) {
            files[i] = files[i].substring(0, files[i].length() - 4);
        }
        return files;
    }

}
