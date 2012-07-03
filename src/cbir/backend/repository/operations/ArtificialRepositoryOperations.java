package cbir.backend.repository.operations;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.Config;
import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Endmember;

public class ArtificialRepositoryOperations extends RepositoryOperations {
	private static final Logger logger = LoggerFactory
			.getLogger(ArtificialRepositoryOperations.class);

	private ImageIdentifier[] contents;
	private final int samples, lines, bands;

	public ArtificialRepositoryOperations(String name, int files, int samples,
			int lines, int bands)
			throws IOException {
		super(name);
		this.samples = samples;
		this.lines = lines;
		this.bands = bands;

		contents = new ImageIdentifier[files];
		for (int i = 0; i < files; i++) {
			contents[i] = new ImageIdentifier("<ART>" + name + "<" + i + ">");
		}
	}

	@Override
	public EnviHeader readHeader(String uuid) {
		return new EnviHeader(uuid, samples, lines, bands);
	}

	// @Override
	// public boolean hasEndmembers(String uuid) {
	// return true;
	// }

	// @Override
	// public EndmemberSet readEndmembers(String uuid) {
	// Random random = new Random(uuid.hashCode());
	// Endmember[] ems = new Endmember[getNPrincipalComponents() + 1];
	// for (int i = 0; i < ems.length; i++) {
	// ems[i] = createEndmember(artHeader.getDimensions().numBands, random);
	// }
	// EndmemberSet endmembers = new EndmemberSet(uuid, ems);
	// return endmembers;
	// }

	private Endmember createEndmember(int dimensions, Random random) {
		/*
		 * based on: R.Y. Rubinstein - Generating random vectors uniformly
		 * distributed inside and on the surface of different regions (1981)
		 * Adaptation: we map everything to the strictly positive segment of the
		 * hypersphere
		 */

		float r = 0;
		float[] v = new float[dimensions];
		for (int i = 0; i < dimensions; i++) {
			v[i] = Math.abs((float) random.nextGaussian());
			r += v[i] * v[i];
		}
		r = (float) Math.sqrt(r);
		for (int i = 0; i < dimensions; i++) {
			v[i] /= r;
		}
		return new Endmember(v);
	}

	@Override
	public FloatImage loadData(EnviHeader header) {
		String uuid = header.getID().getName();
		Random random = new Random(uuid.hashCode());

		Dimensions dim = header.getDimensions();
		float[] data = new float[dim.numElements()];
		// assemble image out of (getNPrincipalComponents + 1) pseudo-random
		// endmembers
		long time = 0;
		if (logger.isDebugEnabled()) {
			time = System.nanoTime();
		}
		for (int i = 0; i < Config.nPrincipalComponents + 1; i++) {
			float[] endmember = createEndmember(dim.numBands, random)
					.getElements();
			float endmemberWeight = random.nextFloat();
			int index = 0;
			for (int j = 0; j < dim.linesSamples(); j++) {
				Float weight = (endmemberWeight + random.nextFloat()) / 2;
				for (int k = 0; k < dim.numBands; k++) {
					data[index] = weight * endmember[k];
					index++;
				}
			}
		}
		if (logger.isDebugEnabled()) {
			time = System.nanoTime() - time;
			logger.debug("Image generation took " + time / 1000000 + "ms");
		}
		return new FloatImage(header, data);
	}

	@Override
	public ImageIdentifier[] contents() throws IOException {
		return contents.clone();
	}

	@Override
	public BufferedImage createBufferedImage(EnviHeader header, String uuid,
			int red, int green, int blue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
