package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.Dimensions;
import cbir.envi.FloatImage;

public class NFindrActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;

	private static final Logger logger = LoggerFactory
			.getLogger(NFindrActivity.class);

	private FloatImage inputImage;
	private int numPrincipalComponents;
	private boolean generateRandomValues;
	private String nFinderInitFile;
	private final ActivityIdentifier[] targets;

	public NFindrActivity(int numPrincipalComponents,
			boolean generateRandomValues, String nFinderInitFile, boolean restrictToLocal,
			ActivityIdentifier... targets) {
		super(Contexts.nFindr, restrictToLocal, true);
		this.numPrincipalComponents = numPrincipalComponents;
		this.generateRandomValues = generateRandomValues;
		this.nFinderInitFile = nFinderInitFile;
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

			int[] nFindrResult = getKernels().nFindr(inputImage.getImageData(),
					dimensions.numSamples, numPrincipalComponents,
					dimensions.linesSamples(), generateRandomValues,
					nFinderInitFile);

			send(new NFindrResult(nFindrResult), targets);
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
