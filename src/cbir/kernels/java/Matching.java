package cbir.kernels.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.MatchTable;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;

public class Matching extends JavaKernel implements cbir.kernels.Matching {

	private static final Logger logger = LoggerFactory
			.getLogger(Matching.class);
	
	/**
	 * 
	 * @param endmembers
	 * @param numEndmembers
	 * @param references
	 * @param numReferences
	 * @param numBands
	 * @return A table of matched endMembers. One entry per row. Columns: [SAD,
	 *         endmemberIndex, referenceIndex] Table is sorted to last column
	 */
	public MatchTable exec(EndmemberSet endmembers, EndmemberSet references) {
//		long start = System.nanoTime();
		Endmember[] endmemberArray = endmembers.getEndmembers();
		Endmember[] referenceArray = references.getEndmembers();
		float[][] angles = new float[endmemberArray.length][referenceArray.length];
		for (int i = 0; i < endmemberArray.length; i++) { // endmember
			float[] endmember = endmemberArray[i].getElements();
			for (int j = 0; j < referenceArray.length; j++) { // firma
				float[] reference = referenceArray[j].getElements();
				angles[i][j] = angulo(endmember, reference);
			}
		}

		MatchTable table = MatchTable.createFromAngles(endmembers.getImageID(), references.getImageID(), angles, referenceArray.length);
		table.sortByReferences();
//		table.printAngles();

//		System.out.printf("Matching\t%f s\n",
//				elapsedTime(start, System.nanoTime()));
//		System.out.println("Score: " + table.getScore());
		
		table.getScore();
		return table;
	}

	private float angulo(float[] a, float[] b) {
		double tita = 0;
		float cos = 0;
		double abstita = 0;
		cos = dot_product(a, b) / (two_norm(a) * two_norm(b)); // calculamos el
																// coseno del
																// angulo entre
																// ambos
		if (cos >= 1)
			return 0;
		tita = Math.acos(cos); // calculamos el angulo como el arcoseno
		if (tita < 0)
			abstita = tita * -1; // lo devolvemos en valor absoluto (positivo)
		else
			abstita = tita;

		return (float) abstita;

	}
	private float dot_product(float[] a, float[] b) {
		int elements = a.length;
		if(a.length != b.length) {
			logger.warn("dot_product - incompatible dimensions: " + a.length + ", " + b.length);
			elements = Math.min(a.length, b.length);
		}
		float result = 0;
		for (int i = 0; i < elements; i++) {
			result += a[i] * b[i];
		}
		return result;
	}

	private float two_norm(float[] a) {
		return (float) Math.sqrt(dot_product(a, a));
	}

}
