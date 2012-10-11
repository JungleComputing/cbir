package cbir.node;

import ibis.constellation.Executor;

import java.util.Arrays;
import java.util.Random;

import cbir.Cbir;
import cbir.Config;
import cbir.backend.MetadataStore;
import cbir.backend.store.MetadataStoreImpl;
import cbir.envi.Dimensions;
import cbir.envi.EnviHeader;
import cbir.envi.ImageIdentifier;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;
import cbir.metadata.Metadata;

/**
 * A Store pre-filled with artificial endmember data. Does not load data from
 * repositories
 * 
 * @author Timo van Kessel
 * 
 */
public class ArtificialStoreNode extends Node {

    private static final int ENVI_BANDS = 224;

    private final String[] repositories;
    private final MetadataStore store;
    private final int tileWidth, tileHeight, imagesPerRepository;

    public ArtificialStoreNode(MetadataStore store, int tileWidth, int tileHeight,
            String[] repositories, int imagesPerRepository, Executor... executors) {
        super(executors);
        this.store = store;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.repositories = repositories;
        this.imagesPerRepository = imagesPerRepository;

    }

    @Override
    public void activate() {
        Dimensions dim = new Dimensions(tileHeight,  tileWidth,  ENVI_BANDS);
        for(int i = 0; i < repositories.length; i++) {
            fillStore(store, repositories[i], dim, imagesPerRepository);
        }
        super.activate();
        // ActivityIdentifier id = submit(new
        // StoreTiledUpdateActivity(storeName,
        // tileWidth, tileHeight, repositories));
        // System.out.println("StoreTiledUpdateActivity: " + id + " submitted");

    }

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

    private EndmemberSet createEndmemberSet(ImageIdentifier imageID,
            int numBands) {
        Random random = new Random(imageID.hashCode());
        Endmember[] endmembers = new Endmember[Config.nPrincipalComponents + 1];
        for (int i = 0; i < Config.nPrincipalComponents + 1; i++) {
            endmembers[i] = createEndmember(numBands, random);
        }
        return new EndmemberSet(imageID, endmembers);
    }

    private void fillStore(MetadataStore store, String repositoryName,
            Dimensions dimensions, int images) {
        for (int i = 0; i < images; i++) {
            String uuid = String.format("%s<%d>", repositoryName, i);
            Dimensions dim = dimensions.clone();
            EnviHeader header = new EnviHeader(uuid, dim);
            EndmemberSet endmembers = createEndmemberSet(header.getID(), dim.numBands);
            Metadata md = new Metadata(header, endmembers);
            store.put(md, repositoryName);
        }
    }

    public static void main(String[] args) {
        String[] repositories;
        String storeName;

        int executors = Integer.parseInt(args[0]);
        storeName = args[1];
        int width = Integer.parseInt(args[2]);
        int height = Integer.parseInt(args[3]);
        int imagesPerRepository = Integer.parseInt(args[4]); 
        repositories = Arrays.copyOfRange(args, 5, args.length);

        Cbir cbir = new Cbir();

        if (executors < 1) {
            executors = 1;
        }
        MetadataStore store = new MetadataStoreImpl(storeName);
        Executor[] execs = new Executor[executors];
        for (int i = 0; i < executors; i++) {
            execs[i] = cbir.getFactory().createMetadataStoreExecutor(store,
                    repositories);
        }

        ArtificialStoreNode node = new ArtificialStoreNode(store, width,
                height, repositories, imagesPerRepository, execs);
        store.enableUpdates(node);
        node.activate();

        // nothing to do here, just wait for completion...

        node.done();
    }
}
