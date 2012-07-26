package cbir.envi;

import java.io.Serializable;

public class ImageIdentifier implements Serializable,
		Comparable<ImageIdentifier> {

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

	public ImageIdentifier(String uuid, int tileWidth, int tileHeight,
			int vIndex, int hIndex) {
		this.baseImageUuid = uuid;
		isTile = true;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.vIndex = vIndex;
		this.hIndex = hIndex;

		tileUuid = String.format("%s_[%dx%d](%d,%d)", uuid, tileWidth,
				tileHeight, vIndex, hIndex);
	}

	public String getName() {
		if (isTile) {
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

	// public String getUuid() {
	// return uuid;
	// }

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
	
	public String getPrettyName() {
		String label = getBaseImageUuid();

		// label is of format f<yymmdd>t<##>p<##>r<run##>rdn_b_sc01_ort_img
		// f---> date of run
		// r --> run #

		// discard tail
		StringBuffer sbuf = new StringBuffer(label.substring(1, 16));
		// delete t and p, they do not contain any info
		sbuf.delete(6, 12); // <yymmdd>r<##>

		sbuf.insert(7, "un ").insert(6, ": ").insert(4, '-').insert(2, '-')
				.insert(0, "20");
		// sbuf contains "<yyyy>-<mm>-<dd>: run ##" right now

		if (isTile()) {
			// append tile index to sbuf
			sbuf.append(", tile ").append('[').append(getVIndex())
					.append(',').append(getHIndex()).append(']');

		}
		return sbuf.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ImageIdentifier) {
			return getName().equals(((ImageIdentifier) obj).getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public int compareTo(ImageIdentifier other) {
		int result = getBaseImageUuid().compareTo(other.getBaseImageUuid());
		if (result != 0) {
			// base image differs, order alphabetically
			return result;
		}
		if (isTile()) {
			if (other.isTile()) {
				// both tiles
				int vDiff = vIndex - other.vIndex;
				if(vDiff != 0) {
					// we are on different rows
					return vDiff;
				} else {
					// same row, look at column position
					return hIndex - other.hIndex;
				}
			} else {
				// other is my base --> I am more
				return 1 + vIndex + hIndex;
			}
		} else {
			if (other.isTile()) {
				// I am the base of other --> I am less
				return -1 - other.vIndex - other.hIndex;
			} else {
				// both base images --> we are the same!
				return 0;
			}
		}
	}
}
