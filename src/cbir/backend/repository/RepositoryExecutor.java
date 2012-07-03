package cbir.backend.repository;

import java.io.IOException;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.kernels.KernelExecutor;

/**
 * @author Timo van Kessel
 * 
 */
public class RepositoryExecutor extends KernelExecutor implements Repository {

	protected final RepositoryOperations ops;
	private final String name;

	public RepositoryExecutor(String repositoryName, RepositoryOperations ops,
			StealPool belongsTo, StealPool stealsFrom, WorkerContext context,
			boolean useGPU) {
		super(belongsTo, stealsFrom, context, useGPU);
		this.ops = ops;
		name = repositoryName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7651257014883507883L;

	@Override
	public EnviHeader getHeader(ImageIdentifier imageID) {
		try {
			EnviHeader header = ops.readHeader(imageID.getBaseImageUuid());
			if(imageID.isTile()) {
				return header.createTileHeader(imageID);
			} else {
				return header;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PreviewImage getPreview(EnviHeader header, int red, int green, int blue) {
		FloatImage image = getImage(header);
		return new PreviewImage(image, red, green, blue);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public FloatImage getImage(EnviHeader header) {
		try {
			return ops.loadData(header);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
