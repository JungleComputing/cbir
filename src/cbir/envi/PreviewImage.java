package cbir.envi;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class PreviewImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3698783010224944023L;

	private final ImageIdentifier imageID;
	private final int[] imageData;
	private final int red, green, blue, width, height;
	private transient BufferedImage preview;

	public PreviewImage(FloatImage image, int red, int green,
			int blue) {
		this.imageID = image.getID();
		this.red = red;
		this.green = green;
		this.blue = blue;
		Dimensions d = image.getDimensions();
		width = d.numSamples;
		height = d.numLines;
		imageData = image.getRGBArray(red, green, blue);
		preview = null;
	}

	public int[] getChannels() {
		return new int[] { red, green, blue };
	}

	public ImageIdentifier getImageID() {
		return imageID;
	}
	
	public BufferedImage getImage() {
		if (preview == null) {
			preview = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			preview.setRGB(0, 0, width, height, imageData, 0, width);
		}
		return preview;
	}
	
	@Override
	public String toString() {
		return String.format("PreviewImage<%s>", getImageID());
	}
}
