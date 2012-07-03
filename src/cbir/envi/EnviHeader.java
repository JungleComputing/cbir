package cbir.envi;

import java.io.Serializable;
import java.nio.ByteOrder;

public class EnviHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4283406213039359122L;

	public enum Interleave {
		BSQ, BIP, BIL
	}; // bsq = band sequential, bip = band interleaved by pixel, bil = band
		// interleaved by line

	private final ImageIdentifier id;
	// private final String uuid;
	private final String description;
	private final int samples;
	private final int lines;
	private final int bands;
	private final int offset;
	private final String filetype;
	private final DataType datatype;
	private final Interleave interleave;
	private final String sensortype;
	private final boolean bigEndian;
	private final String mapinfo;
	private final String wavelengthunits;
	private final String[] bandnames;

	private final float[] wavelengths;
	private final float[] fwhm;

	// private boolean isTile;
	// private int xStart, yStart, xSize, ySize;
	// private String originalUuid;

	public EnviHeader(String uuid, String description, int samples, int lines,
			int bands, int offset, String filetype, DataType datatype,
			Interleave interleave, String sensortype, ByteOrder byteOrder,
			String mapinfo, String wavelengthunits, String[] bandnames,
			float[] wavelengths, float[] fwhm) {
		id = new ImageIdentifier(uuid);
		// this.uuid = originalUuid = uuid;
		this.description = description;
		this.lines = lines;
		this.samples = samples;
		this.bands = bands;
		this.datatype = datatype;
		this.offset = offset;
		this.filetype = filetype;
		this.interleave = interleave;
		this.sensortype = sensortype;
		bigEndian = byteOrder.equals(ByteOrder.BIG_ENDIAN); // the ByteOrder
															// class is not
															// Serializable :-(
		this.mapinfo = mapinfo;
		this.wavelengthunits = wavelengthunits;
		this.bandnames = bandnames;

		this.wavelengths = wavelengths;
		this.fwhm = fwhm;

		// xStart = yStart = xSize = ySize = -1;
		// isTile = false;
	}

	public EnviHeader(String uuid, int samples, int lines, int bands) {
		this(uuid, null, samples, lines, bands, 0, null, DataType.float32,
				Interleave.BSQ, null, ByteOrder.nativeOrder(), null, null,
				null, null, null);
	}

	public EnviHeader(String uuid, Dimensions dim) {
		this(uuid, null, dim.numSamples, dim.numLines, dim.numBands, 0, null,
				DataType.float32, Interleave.BSQ, null,
				ByteOrder.nativeOrder(), null, null, null, null, null);
	}

	private EnviHeader(EnviHeader parent, int tileWidth, int tileHeight,
			int vIndex, int hIndex) {
		description = parent.description;
		lines = parent.lines;
		samples = parent.samples;
		bands = parent.bands;
		datatype = parent.datatype;
		offset = parent.offset;
		filetype = parent.filetype;
		interleave = parent.interleave;
		sensortype = parent.sensortype;
		bigEndian = parent.bigEndian;
		mapinfo = parent.mapinfo;
		wavelengthunits = parent.wavelengthunits;
		bandnames = parent.bandnames;
		wavelengths = parent.wavelengths;
		fwhm = parent.fwhm;

		id = new ImageIdentifier(parent.getID().getBaseImageUuid(), tileWidth,
				tileHeight, vIndex, hIndex);
		// isTile = true;
		// this.xStart = xStart;
		// this.yStart = yStart;
		// // this.xSize = xSize;
		// // this.ySize = ySize;
		// this.originalUuid = parent.uuid;
		// uuid = String.format("%s_%s", originalUuid, tileName);
	}

	public Dimensions getDimensions() {
		if (isTile()) {
			return new Dimensions(getTileLines(), getTileSamples(), bands);
		} else {
			return getOriginalDimensions();
		}
	}

	protected Dimensions getOriginalDimensions() {
		return new Dimensions(lines, samples, bands);
	}

	public ImageIdentifier getID() {
		return id;
	}

	// public String getUUID() {
	// return uuid;
	// }
	//
	// public String getOriginalUUID() {
	// return originalUuid;
	// }

	public String getDescription() {
		return description;
	}

	public String[] getBandnames() {
		return bandnames;
	}

	public Interleave getInterleave() {
		return interleave;
	}

	public ByteOrder getByteOrder() {
		return bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	public int getDataSize() {
		if (isTile()) {
			return getTileSamples() * getTileLines() * bands
					* datatype.getBytesPerPixel();
		} else {
			return samples * lines * bands * datatype.getBytesPerPixel();
		}
	}

	public DataType getDataType() {
		return datatype;
	}

	public int getTileStartSample() {
		if (!isTile()) {
			return 0;
		}

		return id.getTileWidth() * id.getHIndex();
	}

	public int getTileSamples() {
		if (!isTile()) {
			return samples;
		}
		int overflow = getTileStartSample() + id.getTileWidth() - samples;
		if (overflow > 0) {
			return id.getTileWidth() - overflow;
		} else {
			return id.getTileWidth();
		}
	}

	public int getTileStartLine() {
		if (!isTile()) {
			return 0;
		}
		return id.getTileHeight() * id.getVIndex();
	}

	public int getTileLines() {
		if (!isTile()) {
			return lines;
		}
		int overflow = getTileStartLine() + id.getTileHeight() - lines;
		if (overflow > 0) {
			return id.getTileHeight() - overflow;
		} else {
			return id.getTileHeight();
		}
	}

	public boolean isTile() {
		return id.isTile();
	}

	// public EnviHeader[] createTiles(int tileWidth, int tileHeight) {
	// if (isTile()) {
	// // TODO throw an exception instead?
	// return null;
	// }
	// Dimensions dim = getDimensions();
	// int h = dim.numSamples / tileWidth;
	// int lastTileWidth = dim.numSamples % tileWidth;
	// if (lastTileWidth > 0) {
	// h++;
	// } else {
	// lastTileWidth = tileWidth;
	// }
	// int v = dim.numLines / tileHeight;
	// int lastTileHeight = dim.numLines % tileHeight;
	// if (lastTileHeight > 0) {
	// v++;
	// } else {
	// lastTileHeight = tileHeight;
	// }
	//
	// EnviHeader[] result = new EnviHeader[h * v];
	//
	// int next = 0;
	// int height = tileHeight;
	//
	// int yOffset = 0;
	// try {
	// for (int i = 0; i < v; i++) {
	// if (i == v - 1) {
	// height = lastTileHeight;
	// }
	// int xOffset = 0;
	// for (int j = 0; j < h - 1; j++) {
	// result[next] = createTileHeader(
	// String.format("(%d,%d)%dx%d", j, i, tileWidth, height), xOffset,
	// tileWidth,
	// yOffset, height);
	// next++;
	// xOffset += tileWidth;
	// }
	//
	// result[next] = createTileHeader(String.format("(%d,%d)%dx%d", h-1, i,
	// lastTileWidth, height),
	// xOffset, lastTileWidth, yOffset, height);
	// next++;
	// yOffset += tileHeight;
	// }
	// } catch (EnviFormatException e) {
	// // TODO won't happen when i did this right ;-)
	// e.printStackTrace();
	// System.exit(1);
	// }
	// return result;
	// }

	public EnviHeader[] createTiles(int tileWidth, int tileHeight)
			throws EnviFormatException {
		if (isTile()) {
			// TODO throw an exception instead?
			return null;
		}
		Dimensions dim = getDimensions();
		int h = dim.numSamples / tileWidth;
		if (dim.numSamples % tileWidth > 0) {
			h++;
		}
		int v = dim.numLines / tileHeight;
		if (dim.numLines % tileHeight > 0) {
			v++;
		}

		EnviHeader[] result = new EnviHeader[h * v];

		int next = 0;
		for (int i = 0; i < v; i++) {
			for (int j = 0; j < h; j++) {
				result[next] = createTileHeader(tileWidth, tileHeight, i, j);
				next++;
			}
		}
		return result;
	}

	private EnviHeader createTileHeader(int xSize, int ySize, int vIndex,
			int hIndex) throws EnviFormatException {
		if (isTile()) {
			throw new EnviFormatException("Already a Tile");
		}
		return new EnviHeader(this, xSize, ySize, vIndex, hIndex);
	}

	/**
	 * create tileHeader out of orignal image header
	 * 
	 * @param tileId
	 * @return
	 * @throws EnviFormatException
	 */
	public EnviHeader createTileHeader(ImageIdentifier tileId)
			throws EnviFormatException {
		if (isTile()) {
			throw new EnviFormatException("Already a Tile");
		}
		if(!tileId.isTile()) {
			throw new EnviFormatException("Not a tileIdentifier");
		}
		if(!tileId.getBaseImageUuid().equals(getID().getBaseImageUuid())) {
			throw new EnviFormatException("Tile of another image");
		}
		return new EnviHeader(this, tileId.getTileWidth(), tileId.getTileHeight(), tileId.getVIndex(), tileId.getHIndex());
	}

	public EnviHeader getOriginalHeader() {
		if (isTile()) {
			return new EnviHeader(id.getBaseImageUuid(), description, samples,
					lines, bands, offset, filetype, datatype, interleave,
					sensortype, bigEndian ? ByteOrder.BIG_ENDIAN
							: ByteOrder.LITTLE_ENDIAN, mapinfo,
					wavelengthunits, bandnames, wavelengths, fwhm);
		} else {
			return this;
		}
	}

	public static void main(String[] args) throws EnviFormatException {
		new EnviHeader("sample", 15, 25, 5).createTiles(10, 20);
	}
}
