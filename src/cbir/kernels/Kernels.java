package cbir.kernels;

import cbir.MatchTable;
import cbir.kernels.c.CKernel;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;

public class Kernels {
    private final LSU lsu;
    private final NFindr nFindr;
    private final PCA pca;
    private final SPCA spca;
    private final EndmemberExtraction endmemberExtraction;
    private final Matching matching;
    
    // statistics
    private long lsuTasks = 0;
    private long lsuTime = 0;
    private long nFindrTasks = 0;
    private long nFindrTime = 0;
    private long pcaTasks = 0;
    private long pcaTime = 0;
    private long spcaTasks = 0;
    private long spcaTime = 0;
    private long endExTasks = 0;
    private long endExTime = 0;
    private long matchTasks = 0;
    private long matchTime = 0;


    public static Kernels getKernels(boolean java, boolean c) {
        // load java kernels
        LSU lsu = null;
        NFindr nFindr = null;
        PCA pca = null;
        SPCA spca = null;
        EndmemberExtraction endmemberExtraction = null;
        Matching matching = null;

        if (java) {
            endmemberExtraction = new cbir.kernels.java.EndmemberExtraction();
            matching = new cbir.kernels.java.Matching();
        }

        if (c && CKernel.available()) {
            // replace existing kernels with C-kernels
            pca = new cbir.kernels.c.PCA();
            spca = new cbir.kernels.c.SPCA();
            nFindr = new cbir.kernels.c.NFindr();
            lsu = new cbir.kernels.c.LSU();
        }

        return new Kernels(pca, spca, nFindr, lsu, endmemberExtraction,
                matching);
    }

    public static Kernels getKernels(boolean java, boolean c, long cudaHandle) {
        // load java kernels
        LSU lsu = null;
        NFindr nFindr = null;
        PCA pca = null;
        SPCA spca = null;
        EndmemberExtraction endmemberExtraction = null;
        Matching matching = null;

        if (java) {
            endmemberExtraction = new cbir.kernels.java.EndmemberExtraction();
            matching = new cbir.kernels.java.Matching();
        }

        if (c && CKernel.available()) {
            // replace existing kernels with C-kernels
            pca = new cbir.kernels.c.PCA();
        }

        spca = new cbir.kernels.cuda.SPCA(cudaHandle);
        nFindr = new cbir.kernels.cuda.NFindr(cudaHandle);
        lsu = new cbir.kernels.cuda.LSU(cudaHandle);

        return new Kernels(pca, spca, nFindr, lsu, endmemberExtraction,
                matching);
    }

    private Kernels(PCA pca, SPCA spca, NFindr nFindr, LSU lsu,
            EndmemberExtraction endmemberExtraction, Matching matching) {
        this.pca = pca;
        this.spca = spca;
        this.nFindr = nFindr;
        this.lsu = lsu;
        this.endmemberExtraction = endmemberExtraction;
        this.matching = matching;
    }

    /**
     * 
     * @param image
     * @param P
     * @param num_lines
     * @param num_samples
     * @param num_bands
     * @param lines_samples
     * @return float* Ab
     */
    public float[] lsu(float[] image, int[] P, int num_lines, int num_samples,
            int num_bands, int lines_samples, int num_endmembers) {
        long nanos = -System.nanoTime();
        float[] result = lsu.exec(image, P, num_lines, num_samples, num_bands,
                lines_samples, num_endmembers);
        nanos += System.nanoTime();
        lsuTime+= nanos;
        lsuTasks++;
        return result;
    }

    public long lsuTasks() {
        return lsuTasks;
    }
    
    public long lsuTime() {
        return lsuTime;
    }
    
    public long lsuAvgTime() {
        return lsuTasks == 0 ? 0 : lsuTime/lsuTasks;
    }
    
    /**
     * 
     * @param _image
     * @param num_samples
     * @param num_principal_components
     * @param lines_samples
     * @param generateRandomValues
     * @param nfinder_init_file
     * @return int *P, and null when nfinder fails
     */
    public int[] nFindr(float[] image, int num_samples,
            int num_principal_components, int lines_samples,
            boolean generateRandomValues, String nfinder_init_file) {
        long nanos = -System.nanoTime();
        int[] result = nFindr.exec(image, num_samples, num_principal_components,
                lines_samples, generateRandomValues, nfinder_init_file);
        nanos += System.nanoTime();
        nFindrTime += nanos;
        nFindrTasks++;
        return result;
    }

    public long nFindrTasks() {
        return nFindrTasks;
    }
    
    public long nFindrTime() {
        return nFindrTime;
    }
    
    public long nFindrAvgTime() {
        return nFindrTasks == 0 ? 0 : nFindrTime/nFindrTasks;
    }
    
    public float[] pca(float[] inputImage, int numLines, int numSamples,
            int numBands, int linesSamples) {
        long nanos = -System.nanoTime();
        float[] result = pca.exec(inputImage, numLines, numSamples, numBands,
                linesSamples);
        nanos += System.nanoTime();
        pcaTime+= nanos;
        pcaTasks++;
        return result;
    }

    public long pcaTasks() {
        return pcaTasks;
    }
    
    public long pcaTime() {
        return pcaTime;
    }
    
    public long pcaAvgTime() {
        return pcaTasks  == 0 ? 0 : pcaTime/pcaTasks;
    }

    public float[] spca(float[] inputImage, int numLines, int numSamples,
            int numBands, int linesSamples, int numPrincipalComponents,
            boolean generate, String randomVectorFile,
            boolean fixedNumIterations, int numIterations) {
        long nanos = -System.nanoTime();
        float[] result = spca.exec(inputImage, numLines, numSamples, numBands,
                linesSamples, numPrincipalComponents, generate,
                randomVectorFile, fixedNumIterations, numIterations);
        nanos += System.nanoTime();
        spcaTime+= nanos;
        spcaTasks++;
        return result;
    }

    public long spcaTasks() {
        return spcaTasks;
    }
    
    public long spcaTime() {
        return spcaTime;
    }
    
    public long spcaAvgTime() {
        return spcaTasks  == 0 ? 0 : spcaTime/spcaTasks;
    }

    public Endmember[] getEndmembers(float[] image, int[] P, int numBands,
            int linesSamples, int numEndmembers) {
        long nanos = -System.nanoTime();
        Endmember[] result = endmemberExtraction.exec(image, P, numBands, linesSamples,
                numEndmembers);
        nanos += System.nanoTime();
        endExTime+= nanos;
        endExTasks++;
        return result;
    }

    public long endExTasks() {
        return endExTasks;
    }
    
    public long endExTime() {
        return endExTime;
    }
    
    public long endExAvgTime() {
        return endExTasks == 0 ? 0 : endExTime/endExTasks;
    }

    // public double[][] match(double[] endmembers, int numEndmembers,
    // double[] references, int numReferences, int numBands) {
    // return matching.exec(endmembers, numEndmembers, references,
    // numReferences, numBands);
    // }

    public MatchTable match(EndmemberSet endmembers, EndmemberSet references) {
        // System.out.println("match:");
        // endmembers.print();
        // references.print();
        long nanos = -System.nanoTime();
        MatchTable result = matching.exec(endmembers, references);
        nanos += System.nanoTime();
        matchTime += nanos;
        matchTasks++;
        // result.printAngles();
        // System.out.println("score: " + result.getScore());
        return result;
    }

    public float matchScore(EndmemberSet endmembers, EndmemberSet references) {
        MatchTable result = match(endmembers, references);
        return result.getScore();
    }

    public long matchTasks() {
        return matchTasks;
    }
    
    public long matchTime() {
        return matchTime;
    }
    
    public long matchAvgTime() {
        
        return matchTasks == 0 ? 0 : matchTasks == 0 ? 0 : matchTime/matchTasks;
    }
    
    

    // public float[][] match(float[] endmembers, int numEndmembers,
    // float[] references, int numReferences, int numBands) {
    // return matching.exec(endmembers, numEndmembers, references,
    // numReferences, numBands);
    // }

    // public double[][] match(double[][] endmembers, double[][] references) {
    // return matching.exec(endmembers, references);
    // }
}
