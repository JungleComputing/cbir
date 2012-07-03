package cbir.backend.activities;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.Config;
import cbir.backend.repository.RepositoryExecutor;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.kernels.activities.EndmemberExtractionActivity;
import cbir.kernels.activities.NFindrActivity;
import cbir.kernels.activities.SpcaActivity;
import cbir.metadata.EndmemberSet;
import cbir.metadata.Metadata;

/**
 * @author Timo van Kessel
 * 
 */
public class GenerateFeatureVectors extends RepositoryActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7390539062855723931L;

	private static final boolean LOCAL_SUBACTIVITIES = false;

	private static final Logger logger = LoggerFactory
			.getLogger(GenerateFeatureVectors.class);

	private final ActivityIdentifier[] targets;
	private EnviHeader header;
	private FloatImage image;

	public GenerateFeatureVectors(EnviHeader header, String[] repositories,
			ActivityIdentifier... targets) {
		super(createContext(repositories), false, true);
		this.targets = targets;
		this.header = header;
		image = null;
	}

//	public GenerateFeatureVectors(FloatImage image, String[] repositories,
//			ActivityIdentifier... targets) {
//		super(createContext(repositories), false, true);
//
//		this.targets = targets;
//		this.image = image;
//		this.header = image.getHeader();
//	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		RepositoryExecutor e = getExecutor();
		if(image == null) {
			image = e.getImage(header);
		}

		ActivityIdentifier extractionId = e
				.submit(new EndmemberExtractionActivity(image,
						LOCAL_SUBACTIVITIES, identifier()));

		ActivityIdentifier nFindrId = e.submit(new NFindrActivity(
				Config.nPrincipalComponents, Config.nFindrRandomValues,
				Config.nFindrInitFile, LOCAL_SUBACTIVITIES, extractionId));

		ActivityIdentifier spcaId = e.submit(new SpcaActivity(image, 
				Config.nPrincipalComponents, Config.spcaGenerate,
				Config.spcaVectorFile, Config.spcaIterations,
				LOCAL_SUBACTIVITIES, nFindrId));
		send(image, spcaId);
		suspend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ibis.constellation.Activity#process(ibis.constellation.Event)
	 */
	@Override
	public void process(Event e) throws Exception {
		if (e.data instanceof EndmemberSet) {
			Metadata md = new Metadata(header, (EndmemberSet) e.data);
			if (logger.isDebugEnabled()) {
				logger.debug("EndmemberSet for " + header.getID().getName()
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
