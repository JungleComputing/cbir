package cbir.envi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cbir.MatchTable;

public class TableWriter {

	public static void writeTable(String filename, MatchTable table, boolean S)
			throws IOException {
		File file = new File(filename + ".csv");
		FileWriter writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write("\"" + filename + "\"");
		
		for (MatchTable.Entry entry : table.getTable()) {
			if (entry.angle > 0.1 && S) {
				bw.write(",\"----\"");
			} else {
				bw.write("," + entry.angle);
			}
			System.out.println("Firma de verdad " + entry.referenceIndex
					+ " -- Endmember resultado: " + entry.endmemberIndex);
		}
		bw.close();
		writer.close();
	}
}
