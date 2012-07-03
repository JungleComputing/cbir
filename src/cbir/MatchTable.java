package cbir;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import cbir.envi.ImageIdentifier;

public class MatchTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8627605773558290224L;

	/**
	 * Sorts table entries based on the third value (firma index) Note: this
	 * comparator imposes orderings that are inconsistent with equals.
	 * 
	 * @author Timo van Kessel
	 * 
	 */
	private static class TableEntryComparatorByReference implements
			Comparator<Entry> {

		@Override
		public int compare(Entry element1, Entry element2) {
			return element2.referenceIndex - element1.referenceIndex;
		}
	}

	private static class TableEntryComparatorByEndmember implements
			Comparator<Entry> {

		@Override
		public int compare(Entry element1, Entry element2) {
			return element2.endmemberIndex - element1.endmemberIndex;
		}
	}

	private static class TableEntryComparatorByAngle implements
			Comparator<Entry> {

		@Override
		public int compare(Entry element1, Entry element2) {
			return Float.compare(element1.angle, element2.angle);
		}
	}

	// private static final TableElementComparatorFloat comparatorFloat = new
	// TableElementComparatorFloat();

	public static class Entry implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8518639224600528935L;
		public float angle;
		public int endmemberIndex;
		public int referenceIndex;

		Entry(float angle, int endmemberIndex, int referenceIndex) {
			this.angle = angle;
			this.endmemberIndex = endmemberIndex;
			this.referenceIndex = referenceIndex;
		}
	}

	public static MatchTable createFromAngles(ImageIdentifier endmemberImageID,
			ImageIdentifier referenceImageID, float[][] angles, int numReferences) {
		MatchTable table = new MatchTable(endmemberImageID, referenceImageID,
				numReferences);
		for (int i = 0; i < numReferences; i++) {
			Entry entry = minValue(angles);
			table.set(i, entry);
			removeRow(angles, (int) entry.endmemberIndex);
			removeCol(angles, (int) entry.referenceIndex);
		}

		return table;
	}

	private static void removeRow(float[][] angles, int row) {
		for (int i = 0; i < angles[row].length; i++) {
			angles[row][i] = Float.MAX_VALUE;
		}
	}

	private static void removeCol(float[][] angles, int column) {
		for (int i = 0; i < angles.length; i++) {
			angles[i][column] = Float.MAX_VALUE;
		}
	}

	/**
	 * 
	 * @param M
	 * @param cols
	 * @param rows
	 * @param ypos
	 * @param xpos
	 * @return double[3] {SAD, endmemberIndex, referenceIndex}
	 */
	private static Entry minValue(float[][] M) {
		Entry result = new Entry(Float.MAX_VALUE, -1, -1);
		for (int i = 0; i < M.length; i++) { // rows
			for (int j = 0; j < M[0].length; j++) { // cols
				if (M[i][j] < result.angle) {
					result.angle = M[i][j];
					result.endmemberIndex = i; // yposEndmember
					result.referenceIndex = j; // xposFirma
				}
			}
		}
		return result;
	}

	private Entry[] table;
	private float score;
	private final ImageIdentifier endmemberImageID;
	private final ImageIdentifier referenceImageID;

	private MatchTable(ImageIdentifier endmemberImageID, ImageIdentifier referenceImageID, int size) {
		table = new Entry[size];
		score = Float.NaN;
		this.endmemberImageID = endmemberImageID;
		this.referenceImageID = referenceImageID;
	}

	private void set(int index, Entry entry) {
		table[index] = entry;
	}

	public void sortByReferences() {
		Entry[] sortedTable = new Entry[table.length];
		for (int i = 0; i < table.length; i++) {
			int j = 0;
			while (table[j].referenceIndex != i) {
				j++;
			}
			sortedTable[i] = table[j];
		}
	}

	public void sortByEndmembers() {
		Arrays.sort(table, new TableEntryComparatorByEndmember());
	}

	public void sortByAngles() {
		Arrays.sort(table, new TableEntryComparatorByAngle());
	}

	public Entry[] getTable() {
		return table;
	}

	public float getScore() {
		if (Float.isNaN(score)) {
			calculateScore();
		}
		return score;
	}

	private void calculateScore() {
		//sum of the angles
		score = 0;
		int i = 0;
//		sortByAngles();
		for (Entry entry : table) {
			if (entry != null) {
				score += entry.angle;
				i++;
			} else {
				System.out.println("null entry!");
			}
			
		}
		if (i == 0) {
			// no entries in table
			System.out.println("Empty table for " + referenceImageID() + "!");
//			score = -1;
		} else {
//			score /= i;
		}
	}
		
//	private void calculateScore() {
//		// somehow weighted sum of angles
//		score = 0;
//		int i = 0;
//		sortByAngles();
//		for (Entry entry : table) {
//			if (entry != null) {
//				score += entry.angle;
//				i++;
//			} else {
//				System.out.println("null entry!");
//			}
//			
//		}
//		if (i == 0) {
//			// no entries in table
//			System.out.println("Empty table for " + referenceName() + "!");
//			score = -1;
//		} else {
//			score /= i;
//		}
//	}

	public ImageIdentifier endmemberImageID() {
		return endmemberImageID;
	}

	public ImageIdentifier referenceImageID() {
		return referenceImageID;
	}

	public void printAngles() {
		for (Entry e : table) {
			System.out.println(e.angle);
		}
	}

}
