package cbir.envi;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 
 * @author Timo van Kessel
 *
 * An Image consisting of float pixels stored in a BSQ format
 */
public class FloatImage implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8086533980845791157L;
	
	private EnviHeader header;
	private float[] imageData;
	
	public FloatImage(EnviHeader header, float[] imageData) {
		this.header = header;
		this.imageData = imageData;
	}
	
//	public String getUUID() {
//		return header.getID().getName();
//	}
	
	public ImageIdentifier getID() {
		return header.getID();
	}
	
	public Dimensions getDimensions() {
		return header.getDimensions();
	}
	
	public EnviHeader getHeader() {
		return header;
	}
	
	public float[] getImageData() {
		return imageData;
	}
	
	public void setImageData(float[] imageData) {
		this.imageData = imageData;
	}
	
	private float[][] getBands(int... bands) {
		int bandSize = header.getDimensions().linesSamples();
		float[][] result = new float[bands.length][];
		
		for(int i = 0; i <bands.length; i++) {
			int start = bands[i] * bandSize;
			result[i] = Arrays.copyOfRange(imageData, start, start + bandSize);
		}
		
		return result;
	}
	
	/**
	 * Creates an array of pixel encoded in the {@link BufferedImage.TYPE_INT_ARGB} format
	 * @param red the red channel
	 * @param green the green channel
	 * @param blue the blue channel
	 * @return
	 */
	protected int[] getRGBArray(int red, int green, int blue) {
		float[][] bands = getBands(red, green, blue);
		normalize(bands, 255);
		return toRGBArray(bands[0], bands[1], bands[2]);
	}
	
	public BufferedImage getRGBImage(int red, int green, int blue) {
		int[] rgbArray = getRGBArray(red, green, blue);
		Dimensions dim = header.getDimensions();

		BufferedImage result = new BufferedImage(dim.numSamples, dim.numLines,
				BufferedImage.TYPE_INT_ARGB);

		result.setRGB(0, 0, dim.numSamples, dim.numLines, rgbArray, 0, dim.numSamples);
		return result;
	}
	
	private int[] toRGBArray(float[] r, float[] g, float[] b) {
		int pixels = r.length;

		// merge the image elements of the band buffers into a 32-bit argb pixel
		ByteBuffer bb = ByteBuffer.allocate(pixels * 4); //4 byte per pixel
		for (int i = 0; i < pixels; i++) {
			bb.put((byte) 0xFF);
			bb.put((byte) r[i]);
			bb.put((byte) g[i]);
			bb.put((byte) b[i]);
		}
		bb.clear();
		
		//extract the int[] of pixels
		int[] pixelArray = new int[pixels];
		bb.asIntBuffer().get(pixelArray);

		return pixelArray;
	}
	
	/**
	 * Normalize the bands to values between 0 and @param maxvalue individually
	 * 
	 * @param band
	 * @param maxValue
	 *            the maximum value of an element after normalization
	 */
	private void normalize(float[][] bands, float maxValue) {
		for (float[] band : bands) {
			normalize(band, maxValue);
		}

	}

	/**
	 * Normalize the band to values between 0 and @param maxvalue
	 * 
	 * @param band
	 * @param maxValue
	 *            the maximum value of an element after normalization
	 */
	private void normalize(float[] band, float maxValue) {
		float min, max;
		min = max = band[0];

		// find min and max values
		for (int i = 1; i < band.length; i++) {
			if (band[i] < min) {
				min = band[i];
			} else if (band[i] > max) {
				max = band[i];
			}
		}

		float scale = (max - min) / maxValue;

		for (int i = 0; i < band.length; i++) {
			band[i] = (band[i] - min) / scale;
		}
	}
	
	@Override
	public String toString() {
		return String.format("FloatImage<%s>", getID());
	}
	
}
