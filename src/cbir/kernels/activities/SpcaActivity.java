package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;

public class SpcaActivity extends KernelActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307825814045167739L;

	private static final Logger logger = LoggerFactory
			.getLogger(SpcaActivity.class);
	private final ActivityIdentifier[] targets;

	private static final String PRINCIPAL_COMPONENTS_POSTFIX = "<PC>";

	private FloatImage inputImage;
	private final int numPrincipalComponents;
	private final boolean generate, fixedNumIterations;
	private final String randomVectorFile;
	private final int numIterations;

	public SpcaActivity(FloatImage image, int numPrincipalComponents, boolean generate,
			String randomVectorFile, boolean fixedNumIterations, int numIterations, boolean restrictToLocal,
			ActivityIdentifier... targets) {
		super(Contexts.spca, restrictToLocal, false);
		inputImage = image;
		this.numPrincipalComponents = numPrincipalComponents;
		this.generate = generate;
		this.randomVectorFile = randomVectorFile;
		this.numIterations = numIterations;
		this.targets = targets;
		this.fixedNumIterations = fixedNumIterations;
	}

	@Override
	public void initialize() {
		Dimensions dimensions = inputImage.getDimensions();
		float[] pcaResult = getKernels().spca(inputImage.getImageData(),
				dimensions.numLines, dimensions.numSamples,
				dimensions.numBands, dimensions.linesSamples(),
				numPrincipalComponents, generate, randomVectorFile,
				fixedNumIterations, numIterations);
		send(new FloatImage(new EnviHeader(inputImage.getID().getName()
				+ PRINCIPAL_COMPONENTS_POSTFIX, dimensions.numLines,
				dimensions.numSamples, numPrincipalComponents), pcaResult),
				targets);
		finish();
	}

	@Override
	public void process(Event e) {
		logger.debug("Unexpected Event");
		suspend();
	}

	@Override
	public void cancel() {

	}

	@Override
	public void cleanup() throws Exception {
		// empty
	}

}
