package cbir.kernels;

import cbir.MatchTable;
import cbir.kernels.c.CKernel;
import cbir.kernels.cuda.CudaKernel;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;

public class Kernels {
	private final LSU lsu;
	private final NFindr nFindr;
	private final PCA pca;
	private final SPCA spca;
	private final EndmemberExtraction endmemberExtraction;
	private final Matching matching;

	public static Kernels getKernels(boolean java, boolean c, boolean cuda) {
		//load java kernels
		LSU lsu = null;
		NFindr nFindr = null;
		PCA pca = null;
		SPCA spca = null;
		EndmemberExtraction endmemberExtraction = null;
		Matching matching = null;
		
		if(java) {
			endmemberExtraction = new cbir.kernels.java.EndmemberExtraction();
			matching = new cbir.kernels.java.Matching();
		}
		
		if (c && CKernel.available()) {
			//replace existing kernels with C-kernels
			pca = new cbir.kernels.c.PCA();
			spca = new cbir.kernels.c.SPCA();
			nFindr = new cbir.kernels.c.NFindr();
			lsu = new cbir.kernels.c.LSU();
		}
		
		if (cuda && CudaKernel.available()) {
			spca = new cbir.kernels.cuda.SPCA();
			nFindr = new cbir.kernels.cuda.NFindr();
			lsu = new cbir.kernels.cuda.LSU();
		}
		
		return new Kernels(pca, spca, nFindr, lsu, endmemberExtraction, matching);
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
		return lsu.exec(image, P, num_lines, num_samples, num_bands,
				lines_samples, num_endmembers);
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
			int num_principal_components, int lines_samples, boolean generateRandomValues,
			String nfinder_init_file) {
		return nFindr.exec(image, num_samples, num_principal_components,
				lines_samples, generateRandomValues, nfinder_init_file);
	}

	public float[] pca(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples) {
		return pca.exec(inputImage, numLines, numSamples, numBands,
				linesSamples);
	}
	
	public float[] spca(float[] inputImage, int numLines, int numSamples,
			int numBands, int linesSamples, int numPrincipalComponents, boolean generate, String randomVectorFile, boolean fixedNumIterations, int numIterations) {
		return spca.exec(inputImage, numLines, numSamples, numBands, linesSamples, numPrincipalComponents, generate, randomVectorFile, fixedNumIterations, numIterations);
	}

	public Endmember[] getEndmembers(float[] image, int[] P, int numBands,
			int linesSamples, int numEndmembers) {
		return endmemberExtraction.exec(image, P, numBands, linesSamples,
				numEndmembers);
	}

//	public double[][] match(double[] endmembers, int numEndmembers,
//			double[] references, int numReferences, int numBands) {
//		return matching.exec(endmembers, numEndmembers, references,
//				numReferences, numBands);
//	}

	public MatchTable match(EndmemberSet endmembers, EndmemberSet references) {
//		System.out.println("match:");
//		endmembers.print();
//		references.print();
		MatchTable result =  matching.exec(endmembers, references);

//		result.printAngles();
//		System.out.println("score: " + result.getScore());
		return result;
	}
	
	public float matchScore(EndmemberSet endmembers, EndmemberSet references) {
//		System.out.println("match:");
//		endmembers.print();
//		references.print();
		MatchTable result =  matching.exec(endmembers, references);
//		result.printAngles();
//		System.out.println("score: " + result.getScore());
		return result.getScore();
	}

//	public float[][] match(float[] endmembers, int numEndmembers,
//			float[] references, int numReferences, int numBands) {
//		return matching.exec(endmembers, numEndmembers, references,
//				numReferences, numBands);
//	}

//	public double[][] match(double[][] endmembers, double[][] references) {
//		return matching.exec(endmembers, references);
//	}
}
