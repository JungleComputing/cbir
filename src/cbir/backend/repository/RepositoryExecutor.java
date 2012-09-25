package cbir.backend.repository;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;
import cbir.backend.repository.operations.RepositoryOperations;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.kernels.KernelExecutor;
import de.pleumann.antenna.misc.Strings;

/**
 * @author Timo van Kessel
 * 
 */
public class RepositoryExecutor extends KernelExecutor implements Repository {

    private static final Logger logger = LoggerFactory
            .getLogger(RepositoryExecutor.class);

    protected final RepositoryOperations[] ops;

    public RepositoryExecutor(RepositoryOperations[] ops, StealPool belongsTo,
            StealPool stealsFrom, WorkerContext context, boolean useGPU) {
        super(belongsTo, stealsFrom, context, useGPU);
        this.ops = ops.clone();
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 7651257014883507883L;

    @Override
    public EnviHeader getHeader(ImageIdentifier imageID, String... repositories) {

        try {
            EnviHeader header = getOps(repositories).readHeader(
                    imageID.getBaseImageUuid());
            if (imageID.isTile()) {
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
    public PreviewImage getPreview(EnviHeader header, int red, int green,
            int blue, String... repositories) {
        FloatImage image = getImage(header, repositories);
        return new PreviewImage(image, red, green, blue);
    }

    // @Override
    // public final String getName() {
    // return name;
    // }

    @Override
    public FloatImage getImage(EnviHeader header, String... repositories) {
        try {
            return getOps(repositories).loadData(header);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private RepositoryOperations getOps(String... suitableRepositories) {
        for (RepositoryOperations op : ops) {
            String opName = op.getRepositoryName();
            for (String suitableRepository : suitableRepositories) {
                if (opName.equals(suitableRepository)) {
                    logger.debug("RepOperations " + op.getRepositoryName() + " selected for repository " + suitableRepository );
                    return op;
                }
            }
        }
        if(logger.isErrorEnabled()) {
            StringBuilder sb = new StringBuilder("[");
            for(String str: suitableRepositories) {
                sb.append(str);
                sb.append(", ");
            }
            sb.replace(sb.length()-2, sb.length(), "]");
            logger.error("no suitable operation found for Repositories " + sb.toString());
        }
        
        return null;
    }
}
