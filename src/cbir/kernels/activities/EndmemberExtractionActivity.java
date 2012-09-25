package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.envi.Dimensions;
import cbir.envi.FloatImage;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;

public class EndmemberExtractionActivity extends KernelActivity {

    /**
	 * 
	 */
    private static final long serialVersionUID = 6307825814045167739L;

    private static final Logger logger = LoggerFactory
            .getLogger(EndmemberExtractionActivity.class);

    private FloatImage inputImage;
    int[] P; // result of NFindr
    boolean receivedP;
    private final ActivityIdentifier[] targets;

    public EndmemberExtractionActivity(boolean restrictToLocal,
            ActivityIdentifier... targets) {
        super(Contexts.endmemberExtraction, restrictToLocal, true);
        this.P = null;
        receivedP = false;
        this.inputImage = null;
        this.targets = targets;
    }

    public EndmemberExtractionActivity(FloatImage inputImage,
            boolean restrictToLocal, ActivityIdentifier... targets) {
        super(Contexts.endmemberExtraction, restrictToLocal, true);
        this.P = null;
        receivedP = false;
        this.inputImage = inputImage;
        this.targets = targets;
    }

    public EndmemberExtractionActivity(int[] P, boolean restrictToLocal,
            ActivityIdentifier... targets) {
        super(Contexts.endmemberExtraction, restrictToLocal, true);
        this.P = P;
        receivedP = true;
        this.inputImage = null;
        this.targets = targets;
    }

    public EndmemberExtractionActivity(FloatImage inputImage, int[] P,
            boolean restrictToLocal, ActivityIdentifier... targets) {
        super(Contexts.endmemberExtraction, restrictToLocal, false);
        this.P = P;
        receivedP = true;
        this.inputImage = inputImage;
        this.targets = targets;
    }

    private boolean execute() {
        if (receivedP && inputImage != null) {
            if (P == null) {
                // NFindr failed!
                EndmemberSet result = new EndmemberSet(inputImage.getID(), null);
                send(result, targets);
            } else {
                Dimensions dim = inputImage.getDimensions();
                Endmember[] endmembers = getKernels().getEndmembers(
                        inputImage.getImageData(), P, dim.numBands,
                        dim.linesSamples(), P.length);
                EndmemberSet result = new EndmemberSet(inputImage.getID(),
                        endmembers);
                send(result, targets);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initialize() {
        if (execute()) {
            finish();
        } else {
            suspend();
        }
    }

    @Override
    public void process(Event e) throws Exception {
        if (e.data instanceof NFindrResult) {
            P = ((NFindrResult) e.data).getResults();
            receivedP = true;
        } else if (e.data instanceof FloatImage) {
            inputImage = (FloatImage) e.data;
        } else {
            logger.debug("Unexpected event:" + e + ", containing " + e.data);
        }

        if (execute()) {
            finish();
        } else {
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
