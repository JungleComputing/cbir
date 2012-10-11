/**
 * 
 */
package cbir.backend.repository.operations;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.RepositoryDescriptor;
import cbir.envi.EnviHeader;
import cbir.envi.EnviHeader.Interleave;
import cbir.envi.EnviHeaderFilter;
import cbir.envi.EnviIO;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel
 * 
 */
public class GATRepositoryOperations extends RepositoryOperations {

    private static final Logger logger = LoggerFactory
            .getLogger(GATRepositoryOperations.class);

    private URI baseURI;
    private GATContext context;
    private static final String HEADER_EXTENSION = ".hdr";
    // private static final String DATA_EXTENSION = ".bsq";
    private static final String ENDMEMBER_EXTENSION = ".sig";

    /**
	 * 
	 */
    public GATRepositoryOperations(RepositoryDescriptor repository) {
        super(repository.getName());
        baseURI = repository.getBaseURI();
        if (this.baseURI == null) {
            try {
                this.baseURI = URI.create("");
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        initGAT();
        logger.debug("GATRepositoryOperations initialized, baseURI = "
                + this.baseURI.toString());
    }

    private void initGAT() {
        context = new GATContext();

        // TODO fill context?
    }

    private URI headerURI(String uuid) {
        // FIXME URI.resolve() is buggy
        // return baseURI.resolve(uuid + HEADER_EXTENSION);
        try {
            return new URI(baseURI.toString() + uuid + HEADER_EXTENSION);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private URI dataURI(String uuid, Interleave interleave)
            throws GATObjectCreationException, IOException {
        // FIXME URI.resolve() is buggy
        // URI uri = baseURI.resolve(uuid + "." +
        // interleave.name().toLowerCase());
        URI uri = null;
        try {
            uri = new URI(baseURI.toString() + uuid + "."
                    + interleave.name().toLowerCase());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        File file = GAT.createFile(context, uri);
        if (file.exists()) {
            return uri;
        }

        // FIXME URI.resolve() is buggy
        // uri = baseURI.resolve(uuid + "." + interleave.name().toUpperCase());
        try {
            uri = new URI(baseURI.toString() + uuid + "."
                    + interleave.name().toUpperCase());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        file = GAT.createFile(context, uri);
        if (file.exists()) {
            return uri;
        }

        // FIXME URI.resolve() is buggy
        // uri = baseURI.resolve(uuid);
        try {
            uri = new URI(baseURI.toString() + uuid);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        file = GAT.createFile(context, uri);
        if (file.exists()) {
            return uri;
        }

        throw new FileNotFoundException(uuid);
    }

    private URI endmemberURI(String uuid) {
        // FIXME URI.resolve() is buggy
        // return baseURI.resolve(uuid + ENDMEMBER_EXTENSION);
        try {
            return new URI(baseURI.toString() + uuid + ENDMEMBER_EXTENSION);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * cbir.repository.operations.RepositoryOperations#readHeader(java.lang.
     * String)
     */
    @Override
    protected EnviHeader doReadHeader(String uuid) throws IOException {
        try {
            FileInputStream headerIS = GAT.createFileInputStream(context,
                    headerURI(uuid));
            EnviHeader result = EnviIO.readHeader(uuid, headerIS);
            headerIS.close();
            return result;
        } catch (GATObjectCreationException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected FloatImage doLoadData(EnviHeader header) throws IOException {

        // get the original uuid, which is the uuid of the orignal file in case
        // of tile-headers
        String uuid = header.getID().getBaseImageUuid();
        FileInputStream fis;
        try {
            fis = GAT.createFileInputStream(context,
                    dataURI(uuid, header.getInterleave()));
        } catch (GATObjectCreationException e) {
            throw new IOException(e);
        }

        FloatImage result = EnviIO.readData(header, fis);
        fis.close();
        return result;
    }

    // @Override
    // public void writeFloatImage(float[] image, String fileName, int numLines,
    // int numSamples, int numBands) {
    //
    // }

    // @Override
    // public boolean hasEndmembers(String uuid) {
    // try {
    // return GAT.createFile(endmemberURI(uuid)).exists();
    // } catch (GATObjectCreationException e) {
    // return false;
    // }
    // }
    //
    // @Override
    // public EndmemberSet readEndmembers(String uuid) throws IOException {
    // FileInputStream fis;
    // try {
    // fis = GAT.createFileInputStream(context, endmemberURI(uuid));
    // } catch (GATObjectCreationException e) {
    // throw new IOException(e);
    // }
    // EndmemberSet result = EnviIO.readSignature(uuid, fis);
    // fis.close();
    // return result;
    // }

    @Override
    protected ImageIdentifier[] doContents() throws IOException {
        File dir;
        try {
            dir = GAT.createFile(context, baseURI);
        } catch (GATObjectCreationException e) {
            throw new IOException(e);
        }
        String[] uuids = stripExtension(dir.list(new EnviHeaderFilter()));
        ImageIdentifier[] imageIDs = new ImageIdentifier[uuids.length];
        for (int i = 0; i < uuids.length; i++) {
            imageIDs[i] = new ImageIdentifier(uuids[i]);
        }

        return imageIDs;
    }

    @Override
    protected BufferedImage doCreateBufferedImage(EnviHeader header, String uuid,
            int red, int green, int blue) throws IOException {
        FileInputStream fis;
        try {
            fis = GAT.createFileInputStream(context,
                    dataURI(uuid, header.getInterleave()));
        } catch (GATObjectCreationException e) {
            throw new IOException(e);
        }

        BufferedImage result = EnviIO.getBufferedImage(header, fis, red, green,
                blue);
        fis.close();
        return result;
    }

}
