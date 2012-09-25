package cbir.kernels.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.Config;
import cbir.envi.FloatImage;
import cbir.kernels.KernelExecutor;
import cbir.metadata.EndmemberSet;
import cbir.metadata.Metadata;

/**
 * @author Timo van Kessel
 * 
 */
public class CalculateEndmembers extends KernelActivity {

    private static final Logger logger = LoggerFactory
            .getLogger(CalculateEndmembers.class);

    private final ActivityIdentifier[] targets;
    private final FloatImage image;
    private int attempts = 0;

    public CalculateEndmembers(FloatImage image, ActivityIdentifier... targets) {
        super(Contexts.featureExtraction, false, false);
        this.targets = targets;
        this.image = image;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -1671988589204849364L;

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#initialize()
     */
    @Override
    public void initialize() throws Exception {
        startExtraction();
        suspend();
    }

    private void startExtraction() {
        KernelExecutor e = getExecutor();

        ActivityIdentifier extractionId = e
                .submit(new EndmemberExtractionActivity(image, false,
                        identifier()));

        ActivityIdentifier nFindrId = e.submit(new NFindrActivity(
                Config.nPrincipalComponents, Config.nFindrRandomValues,
                Config.nFindrInitFile, false, extractionId));

        ActivityIdentifier spcaId = e.submit(new SpcaActivity(image,
                Config.nPrincipalComponents, Config.spcaGenerate,
                Config.spcaVectorFile, Config.spcaFixedNumIterations,
                Config.spcaIterations, false, nFindrId));
        send(image, spcaId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#process(ibis.constellation.Event)
     */
    @Override
    public void process(Event e) throws Exception {
        if (e.data instanceof EndmemberSet) {
            EndmemberSet eset = (EndmemberSet) e.data;
            if(eset.getEndmembers() == null && ++attempts < Config.EXTRACTION_ATTEMPTS) {
                    //extraction failed, retry
                    startExtraction();
                    suspend();
                    return;    
            }
            Metadata md = new Metadata(image.getHeader(), eset);
            if (logger.isDebugEnabled()) {
                logger.debug("EndmemberSet for " + image.getID()
                        + " received, sending metadata to target");
            }
            send(md, targets);
            finish();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Received an unsupported Event:" + e.toString());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#cleanup()
     */
    @Override
    public void cleanup() throws Exception {
        // empty
    }

    /*
     * (non-Javadoc)
     * 
     * @see ibis.constellation.Activity#cancel()
     */
    @Override
    public void cancel() throws Exception {
        // empty
    }
}
