package cbir.kernels;

import cbir.MatchTable;
import cbir.metadata.EndmemberSet;


public interface Matching {
	
	MatchTable exec(EndmemberSet endmembers, EndmemberSet references);
}
