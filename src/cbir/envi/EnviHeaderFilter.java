package cbir.envi;

import java.io.File;
import java.io.FilenameFilter;

public class EnviHeaderFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.toLowerCase().endsWith(".hdr")/* || name.endsWith(".HDR")*/) {
			return true;
		} else {
			return false;
		}
	}

}
