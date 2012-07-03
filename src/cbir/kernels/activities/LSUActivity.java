package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;

public class LSUActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;

	private static final Logger logger = LoggerFactory
			.getLogger(LSUActivity.class);

	private static final String ABUNDANCE_MAP_POSTFIX = "<AM>";

	private final ActivityIdentifier[] targets;
	private FloatImage inputImage;
	int[] P;

	public LSUActivity(boolean restrictToLocal, ActivityIdentifier... targets) {
		super(Contexts.lsu, restrictToLocal, true);
		inputImage = null;
		P = null;
		this.targets = targets;
	}

	@Override
	public void initialize() {
		suspend();
	}

	@Override
	public void process(Event e) throws Exception {
		if (e.data instanceof int[]) {
			P = (int[]) e.data;
		} else if (e.data instanceof FloatImage) {
			inputImage = (FloatImage) e.data;
		} else {
			logger.debug("Unexpected event");
		}

		if (P != null && inputImage != null) {
			Dimensions dimensions = inputImage.getDimensions();
			float[] lsuResult = getKernels().lsu(inputImage.getImageData(), P,
					dimensions.numLines, dimensions.numSamples,
					dimensions.numBands, dimensions.linesSamples(), P.length);

			send(new FloatImage(new EnviHeader(inputImage.getID().getName()
					+ ABUNDANCE_MAP_POSTFIX, dimensions.numLines,
					dimensions.numSamples, P.length), lsuResult), targets);

			finish();
		} else {
			suspend();
		}
	}

	@Override
	public void cancel() {

	}

	@Override
	public void cleanup() throws Exception {

	}

}
