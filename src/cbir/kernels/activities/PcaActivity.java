package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;

public class PcaActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;

	private static final Logger logger = LoggerFactory
			.getLogger(PcaActivity.class);

	private FloatImage inputImage;
	private final ActivityIdentifier[] targets;


	private static final String PRINCIPAL_COMPONENTS_POSTFIX = "<PC>";

	public PcaActivity(boolean restrictToLocal, ActivityIdentifier... targets) {
		super(Contexts.pca, restrictToLocal, true);
		inputImage = null;
		this.targets = targets;
	}

	@Override
	public void initialize() {
		suspend();
	}

	@Override
	public void process(Event e) {
		if (e.data instanceof FloatImage) {
			inputImage = (FloatImage) e.data;

			Dimensions dimensions = inputImage.getDimensions();
			float[] pcaResult = getKernels().pca(inputImage.getImageData(),
					dimensions.numLines, dimensions.numSamples,
					dimensions.numBands, dimensions.linesSamples());
			EnviHeader header = new EnviHeader(inputImage.getID().getName()
					+ PRINCIPAL_COMPONENTS_POSTFIX, dimensions.numLines, dimensions.numSamples,
					dimensions.numBands);
			send(new FloatImage(header, pcaResult), targets);
			
			finish();
		} else {
			logger.debug("Unexpected Event");
			suspend();
		}
	}

	@Override
	public void cancel() {
		// empty
	}

	@Override
	public void cleanup() throws Exception {
		// empty
	}

}
