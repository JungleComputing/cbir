package cbir.envi;

public enum DataType {
	byte8(1, 1), short16(2, 2), int32(3, 4), float32(4, 4), double64(5, 8), complex2x32(
			6, 8), complex2x64(9, 16), ushort16(12, 2), ulong32(13, 4), long64(
			14, 8), ulong64(15, 8);

	private final int id;
	private final int bpp; // bytes per pixel

	private DataType(int id, int bpp) {
		this.id = id;
		this.bpp = bpp;
	}

	public int getId() {
		return id;
	}

	public int getBytesPerPixel() {
		return bpp;
	}

}