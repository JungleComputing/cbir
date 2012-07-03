package cbir.envi;

import java.io.Serializable;

public class ImageIdentifier implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1651336740045720775L;
	private final String tileUuid;
	private final String baseImageUuid;
	private final boolean isTile;
	private final int tileWidth, tileHeight;
	private final int hIndex, vIndex;
	
	public ImageIdentifier(String uuid) {
		this.baseImageUuid = uuid;
		isTile = false;
		tileUuid = null;
		tileWidth = tileHeight = hIndex = vIndex = -1;
	}
	
	public ImageIdentifier(String uuid, int tileWidth, int tileHeight, int vIndex , int hIndex) {
		this.baseImageUuid = uuid;
		isTile = true;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.vIndex = vIndex;
		this.hIndex = hIndex;
		
		tileUuid = String.format("%s_[%dx%d](%d,%d)", uuid, tileWidth, tileHeight, vIndex, hIndex);
	}
	
	public String getName() {
		if(isTile) {
			return tileUuid;
		} else {
			return baseImageUuid;
		}
	}
	
	public boolean isTile() {
		return isTile;
	}
	
	public int getVIndex() {
		return vIndex;
	}
	
	public int getHIndex() {
		return hIndex;
	}
	
	protected int getTileHeight() {
		return tileHeight;
	}
	
	protected int getTileWidth() {
		return tileWidth;
	}
	
//	public String getUuid() {
//		return uuid;
//	}
	
	public String getTileUuid() {
		return tileUuid;
	}
	
	

	public String getBaseImageUuid() {
		return baseImageUuid;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof ImageIdentifier) {
			return getName().equals(obj.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
