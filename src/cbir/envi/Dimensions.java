package cbir.envi;

import java.io.Serializable;

public class Dimensions implements Serializable, Cloneable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 9005398913008168489L;

    public final int numLines, numSamples, numBands;

    public Dimensions(int numLines, int numSamples, int numBands) {
        this.numLines = numLines;
        this.numSamples = numSamples;
        this.numBands = numBands;
    }

    public int numElements() {
        return numLines * numSamples * numBands;
    }

    public final int linesSamples() {
        return numLines * numSamples;
    }

    @Override
    public Dimensions clone() {
        return new Dimensions(numLines, numSamples, numBands);
    }
}
