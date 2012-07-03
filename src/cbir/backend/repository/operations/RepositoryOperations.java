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

	public String getRepositoryName() {
		return repositoryName;
	}

	public abstract EnviHeader readHeader(String uuid) throws IOException;

	// public abstract String getDataLocation(String imageName);

	/**
	 * 
	 * @param header
	 *            The header corresponding to the image file
	 * @return The image data
	 * @throws IOException
	 */
	public abstract FloatImage loadData(EnviHeader header)
			throws IOException;

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
	public abstract BufferedImage createBufferedImage(EnviHeader header,
			String uuid, int red, int green, int blue) throws UnsupportedOperationException, IOException;

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

	public abstract ImageIdentifier[] contents() throws IOException;

	protected String[] stripExtension(String[] files) {
		for (int i = 0; i < files.length; i++) {
			files[i] = files[i].substring(0, files[i].length() - 4);
		}
		return files;
	}
}
