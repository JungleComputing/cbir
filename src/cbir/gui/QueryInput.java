package cbir.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cbir.RepositoryDescriptor;
import cbir.backend.repository.operations.GATRepositoryOperations;
import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;

public class QueryInput {

    private String file;
    private EnviHeader header;
    private GATRepositoryOperations rops;
    private FloatImage image;
    private boolean local;

    public QueryInput() {
        rops = new GATRepositoryOperations(new RepositoryDescriptor("", null));
        local = true;
        file = "";
        header = null;
    }

    public void setFromFile(String file) {
        this.file = file;
        try {
            header = rops.readHeader(file);

            // FIXME for large image, we only read the center part
            Dimensions dim = header.getDimensions();
            if (dim.numLines > 256 || dim.numSamples > 256) {
                EnviHeader[] headers = header.createTiles(256, 256);
                header = headers[headers.length / 2];
            }

            image = rops.loadData(header);
            local = true;
        } catch (IOException e) {
            e.printStackTrace();
            image = null;
            header = null;
        }
    }

    public void setFromImage(FloatImage image) {
        this.image = image;
        file = "";
        header = image.getHeader();
        local = false;
    }

    public void setFromHeader(EnviHeader header) {
        this.header = header;
        image = null;
        file = "";
        local = false;
    }

    public boolean isLocal() {
        return local;
    }

    public String getFile() {
        return file;
    }

    public ImageIdentifier getID() {
        if(header == null) {
            return null;
        }
        return header.getID();
    }

    public String[] getBandNames() {
        if (!hasHeader()) {
            return null;
        }
        String[] bands = getHeader().getBandnames();
        if (bands == null) {
            bands = getBandNumbers();
        }
        return bands;
    }

    public String[] getBandNumbers() {
        if (!hasHeader()) {
            return null;
        }
        String[] numbers = new String[getHeader().getDimensions().numBands];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = String.valueOf(i + 1);
        }
        return numbers;
    }

    public float[] getWavelengths() {
        if (!hasHeader()) {
            return null;
        }
        return getHeader().getWavelengths();
    }

    public BufferedImage getRGBImage(int red, int green, int blue) {
        return image.getRGBImage(red, green, blue);
    }

    public EnviHeader getHeader() {
        return header;
    }

    public FloatImage getFloatImage() {
        return image;
    }

    public boolean hasImage() {
        return image != null;
    }

    public boolean hasHeader() {
        return header != null;
    }

}
