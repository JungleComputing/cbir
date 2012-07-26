package cbir.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cbir.backend.repository.operations.GATRepositoryOperations;
import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;

public class QueryInput {

	private String file;
	private ImageIdentifier imageID;
	// private EnviHeader header;
	private GATRepositoryOperations rops;
	private FloatImage image;
	private boolean local;

	public QueryInput() {
		rops = new GATRepositoryOperations("", null);
		local = true;
		file = "";
		imageID = null;
	}

	public void setFromFile(String file) {
		this.file = file;
		imageID = new ImageIdentifier(file);
		try {
			EnviHeader header = rops.readHeader(file);
						
			//FIXME for large image, we only read the center part
			Dimensions dim = header.getDimensions();
			if(dim.numLines > 256 || dim.numSamples > 256) {
				EnviHeader[] headers = header.createTiles(256, 256);
				header = headers[headers.length/2];
			}
			
			image = rops.loadData(header);
			local = true;
		} catch (IOException e) {
			e.printStackTrace();
			image = null;
		}
	}
	
	public void setFromImage(FloatImage image) {
		this.image = image;
		file = "";
		imageID = image.getID();
		local = false;
	}
	
	public boolean isLocal() {
		return local;
	}
	
	public String getFile() {
		return file;
	}
	
	public ImageIdentifier getID() {
		return imageID;
	}

	public String[] getBandNames() {
		if(!hasImage()) {
			return null;
		}
		String[] bands = image.getHeader().getBandnames();
		if (bands == null) {
			bands = getBandNumbers();
		}
		return bands;
	}

	public String[] getBandNumbers() {
		if(!hasImage()) {
			return null;
		}
		String[] numbers = new String[image.getHeader().getDimensions().numBands];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = String.valueOf(i+1);
		}
		return numbers;
	}
	
	public float[] getWavelengths() {
		if(!hasImage()) {
			return null;
		}
		return image.getHeader().getWavelengths();
	}

	public BufferedImage getRGBImage(int red, int green, int blue) {
		return image.getRGBImage(red, green, blue);
	}

	public EnviHeader getHeader() {
		return (image == null) ? null : image.getHeader();
	}

	public FloatImage getFloatImage() {
		return image;
	}

	public boolean hasImage() {
		return image != null;
	}

}
