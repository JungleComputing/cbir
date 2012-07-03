package cbir.metadata;

import java.io.Serializable;

import cbir.envi.EnviHeader;
import cbir.envi.ImageIdentifier;
import cbir.metadata.EndmemberSet;

public class Metadata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3304407376780834348L;
	
	private final EndmemberSet endmembers;
	private final EnviHeader header;
	
	public Metadata(EnviHeader header, EndmemberSet endmembers) {
		this.endmembers = endmembers;
		this.header = header;
	}
	
//	public String getUUID() {
//		return header.getID().getName();
//	}
	
	public ImageIdentifier getImageID() {
		return header.getID();
	}
	
	public EnviHeader getHeader() {
		return header;
	}
	
	public EndmemberSet getEndmembers() {
		return endmembers;
	}
	
}
