package cbir.envi;

import java.io.File;
import java.io.FilenameFilter;

public class EnviSigFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.toLowerCase().endsWith(".sig")/* || name.endsWith(".SIG")*/) {
			return true;
		} else {
			return false;
		}
	}

}
