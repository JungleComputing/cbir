package cbir.envi;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import cbir.envi.EnviHeader.Interleave;
import cbir.metadata.Endmember;
import cbir.metadata.EndmemberSet;

public class EnviIO {

	private static final int OUTPUT_FIELDS = 4;

	public static EnviHeader readHeader(String uuid, InputStream headerInput)
			throws IOException {
		String description = null;
		int samples = -1;
		int lines = -1;
		int bands = -1;
		int offset = -1;
		String filetype = null;
		DataType datatype = null;
		Interleave interleave = null;
		String sensortype = null;
		ByteOrder byteOrder = null;
		String mapinfo = null;
		String wavelengthunits = null;
		String[] bandnames = null;

		float[] wavelengths = null;
		float[] fwhm = null;

		Reader r = new InputStreamReader(headerInput);
		BufferedReader br = new BufferedReader(r);

		String line = br.readLine();
		if (!line.startsWith("ENVI")) {
			throw new EnviFormatException(
					"cannot read header: \""
							+ uuid
							+ "\", unsupported input format, first line should be \"ENVI\"");
		}

		while ((line = br.readLine()) != null) {
			if (line.startsWith("description")) {
				// discard this line and read the next lines for the
				// description information
				String temp = "";
				while ((line = br.readLine()) != null
						&& !(line = line.trim()).endsWith("}")) {
					temp += line;
				}
				// remove the trailing '}'
				String last = line.substring(0, line.length() - 1);
				description = temp + last;

			} else if (line.startsWith("samples")) {
				String[] split = line.split("=");
				samples = Integer.parseInt(split[1].trim());

			} else if (line.startsWith("lines")) {
				String[] split = line.split("=");
				lines = Integer.parseInt(split[1].trim());

			} else if (line.startsWith("bands")) {
				String[] split = line.split("=");
				bands = Integer.parseInt(split[1].trim());

			} else if (line.startsWith("header offset")) {
				String[] split = line.split("=");
				offset = Integer.parseInt(split[1].trim());

			} else if (line.startsWith("file type")) {
				String[] split = line.split("=");
				filetype = split[1].trim();

			} else if (line.startsWith("data type")) {
				String[] split = line.split("=");
				int type = Integer.parseInt(split[1].trim());
				switch (type) {
				case 1:
					datatype = DataType.byte8;
					break;
				case 2:
					datatype = DataType.short16;
					break;
				case 3:
					datatype = DataType.int32;
					break;
				case 4:
					datatype = DataType.float32;
					break;
				case 5:
					datatype = DataType.double64;
					break;
				case 6:
					datatype = DataType.complex2x32;
					break;
				case 9:
					datatype = DataType.complex2x64;
					break;
				case 12:
					datatype = DataType.ushort16;
					break;
				case 13:
					datatype = DataType.ulong32;
					break;
				case 14:
					datatype = DataType.long64;
					break;
				case 15:
					datatype = DataType.ulong64;
					break;
				default:
					throw new EnviFormatException("Unknown data type: \""
							+ type + "\" see ENVI file format for details.");
				}
			} else if (line.startsWith("interleave")) {
				String[] split = line.split("=");
				if (split[1].trim().equalsIgnoreCase("bsq")) {
					interleave = Interleave.BSQ;
				} else if (split[1].trim().equalsIgnoreCase("bip")) {
					interleave = Interleave.BIP;
				} else if (split[1].trim().equalsIgnoreCase("bil")) {
					interleave = Interleave.BIL;
				} else {
					throw new EnviFormatException(
							"Unknown interleave format: \"" + split[1]
									+ "\" see ENVI file format for details.");
				}

			} else if (line.startsWith("sensor type")) {
				String[] split = line.split("=");
				sensortype = split[1].trim();

			} else if (line.startsWith("byte order")) {
				String[] split = line.split("=");
				int type = Integer.parseInt(split[1].trim());
				switch (type) {
				case 0:
					byteOrder = ByteOrder.LITTLE_ENDIAN;
					break;
				case 1:
					byteOrder = ByteOrder.BIG_ENDIAN;
					break;

				default:
					throw new EnviFormatException("Unknown byte order: \""
							+ type + "\" see ENVI file format for details.");
				}

			} else if (line.startsWith("map info")) {
				String[] split = line.split("=");
				String temp = split[1].trim();
				// remove the ''{' and '}'
				mapinfo = temp.substring(1, temp.length() - 1);

			} else if (line.startsWith("wavelength units")) {
				String[] split = line.split("=");
				wavelengthunits = split[1].trim();

			} else if (line.startsWith("band names")) {
				// discard this line and read the next lines for the
				// description information
				String temp = "";
				while ((line = br.readLine()) != null && !line.endsWith("}")) {
					temp += line.trim();
				}
				// remove the trailing '}'
				String last = line.substring(0, line.length() - 1);
				temp += last;

				bandnames = temp.split(",");
			} else if (line.startsWith("wavelengths")) {
				// discard this line and read the next lines for the
				// description information
				String temp = "";
				while ((line = br.readLine()) != null && !line.endsWith("}")) {
					temp += line.trim();
				}
				// remove the trailing '}'
				String last = line.substring(0, line.length() - 1);
				temp += last;

				String[] wlString = temp.split(",");

				wavelengths = new float[wlString.length];
				for (int i = 0; i < wlString.length; i++) {
					wavelengths[i] = Float.parseFloat(wlString[i]);
				}
			} else if (line.startsWith("fwhm")) {
				// discard this line and read the next lines for the
				// description information
				String temp = "";
				while ((line = br.readLine()) != null && !line.endsWith("}")) {
					temp += line.trim();
				}
				// remove the trailing '}'
				String last = line.substring(0, line.length() - 1);
				temp += last;

				String[] fwhmString = temp.split(",");

				fwhm = new float[fwhmString.length];
				for (int i = 0; i < fwhmString.length; i++) {
					fwhm[i] = Float.parseFloat(fwhmString[i]);
				}
			}
		}
		br.close();
		r.close();
		return new EnviHeader(uuid, description, samples, lines, bands, offset,
				filetype, datatype, interleave, sensortype, byteOrder, mapinfo,
				wavelengthunits, bandnames, wavelengths, fwhm);
	}

	public static EndmemberSet readSignature(ImageIdentifier imageID,
			InputStream endmemberInputStream) throws IOException {

		Reader r = new InputStreamReader(endmemberInputStream);
		BufferedReader br = new BufferedReader(r);

		String line = br.readLine();
		if (!line.equals("SIG")) {
			throw new EnviFormatException(
					"cannot read endmembers for: \""
							+ imageID
							+ "\", unsupported input format, first line should be \"SIG\"");
		}

		line = br.readLine();
		if (line == null || !line.startsWith("endmembers")) {
			throw new EnviFormatException("Unknown line: \"" + line
					+ "\" see SIG file format for details.");
		}
		String[] split = line.split("=");
		int nEndmembers = Integer.parseInt(split[1].trim());

		line = br.readLine();
		if (line == null || !line.startsWith("bands")) {
			throw new EnviFormatException("Unknown line: \"" + line
					+ "\" see SIG file format for details.");
		}
		split = line.split("=");
		int bands = Integer.parseInt(split[1].trim());

		Endmember[] ems = new Endmember[nEndmembers];
		for (int endmember = 0; endmember < nEndmembers; endmember++) {
			line = br.readLine();
			if (line == null || !line.startsWith("===")) {
				throw new EnviFormatException("Unknown line: \"" + line
						+ "\" see SIG file format for details.");
			}
			float[] data = new float[bands];
			for (int band = 0; band < bands; band++) {
				line = br.readLine();
				if (line == null) {
					throw new EnviFormatException("Unknown line: \"" + line
							+ "\" see SIG file format for details.");
				}

				data[band] = Float.parseFloat(line.trim());
			}
			ems[endmember] = new Endmember(data);
		}
		EndmemberSet endmembers = new EndmemberSet(imageID, ems);
		br.close();
		r.close();
		return endmembers;
	}

	public static FloatImage readData(EnviHeader header, InputStream input)
			throws IOException {
		if (header.isTile()) {
			return readTile(header, input);
		}
		int bytes = header.getDimensions().numElements()
				* header.getDataType().getBytesPerPixel();
		byte[] data = new byte[bytes];
		// simply read everthing in 1 go:
		readStream(input, bytes, data, 0);
		ByteBuffer bb = ByteBuffer.wrap(data).order(header.getByteOrder());

		FloatBuffer floats = toFloatBuffer(bb, header.getDataType());

		float[] floatData = toBSQ(floats, header);
		return new FloatImage(header, floatData);
	}

	private static FloatImage readTile(EnviHeader header, InputStream input)
			throws IOException {
		final int xStart = header.getTileStartSample();
		final int yStart = header.getTileStartLine();
		final int xSize = header.getTileSamples();
		final int ySize = header.getTileLines();

		final int bpp = header.getDataType().getBytesPerPixel();
		
		Dimensions dim = header.getOriginalDimensions();
		
		final int bytes = xSize * ySize * dim.numBands * bpp;
//		System.out.println("READ_TILE");
//		System.out.println(String.format("image (w,h,d): %d x %d x %d", dim.numSamples, dim.numLines, dim.numBands));
//		System.out.println(String.format("tile: %d x %d (%d, %d)", xSize, ySize, xStart, yStart));
//		System.out.println(String.format("%d bytes per image element", bpp));
		byte[] data = new byte[bytes];

		switch (header.getInterleave()) {
		case BIL: {
			long lineSize = dim.numSamples * dim.numBands * bpp;
			int tileBandLine = xSize * bpp;

			long offset = yStart * lineSize + xStart * bpp;
			long bandStride = (dim.numSamples * bpp) - tileBandLine;

			// skip to first byte of the tile
			skip(input, offset);

			final int totalBands = ySize * dim.numBands;
			// read the tile per band, over all lines
			for (int i = 0; i < totalBands; i++) {
				readStream(input, tileBandLine, data, i * tileBandLine);
				// skip the line stride, except after the last line
				if (i != totalBands - 1) {
					skip(input, bandStride);
				}
			}
		}
			break;
		case BIP: {
			//FIXME broken
			long lineSize = ((long)dim.numSamples) * dim.numBands * bpp;
			int tileLine = xSize * dim.numBands * bpp;

			long offset = yStart * lineSize + xStart * dim.numBands * bpp;
			long lineStride = lineSize - tileLine;

//			System.out.println("READ_BIP_TILE");
//			System.out.println(String.format("image: %d x %d", dim.numSamples, dim.numLines));
//			System.out.println(String.format("tile: %d x %d (%d, %d)", xSize, ySize, xStart, yStart));
			// skip to first byte of the tile
//			System.out.println(String.format("skipping %d bytes offset", offset));
			skip(input, offset);

			// read the tile per line
			for (int i = 0; i < ySize; i++) {
//				System.out.println("read line " + i);
//				System.out.println(String.format("readStream(input, %d, data, %d)", tileLine, i * tileLine));
				readStream(input, tileLine, data, i * tileLine);
				// skip the line stride, except after the last line
				if (i != ySize - 1) {
//					System.out.println(String.format("skipping %d bytes stride", lineStride));
					skip(input, lineStride);
				}
			}
		}
			break;
		case BSQ: {
			long lineSize = dim.numSamples * bpp;
			int tileLine = xSize * bpp;
			int tilebandSize = xSize * ySize * bpp;

			long lineStride = lineSize - tileLine;
			long bandStride = (dim.numLines - ySize) * lineSize + lineStride;
			long offset = ((long)yStart) * lineSize + xStart * bpp;

			// skip the initial offset
			skip(input, offset);

			// loop over bands
			for (int i = 0; i < dim.numBands; i++) {
				// and over the lines per band
				for (int j = 0; j < ySize - 1; j++) {
					readStream(input, tileLine, data, i * tilebandSize + j
							* tileLine);
					// skip stride between lines
					skip(input, lineStride);
				}
				// last line is not followed by a stride skip
				readStream(input, tileLine, data, i * tilebandSize
						+ (ySize - 1) * tileLine);

				if (i < dim.numBands - 1) {
					// skip the stride between bands, but do not do that after
					// reading the last band
					skip(input, bandStride);
				}
			}
		}
			break;
		default:
			throw new IOException("Unknown Interleave" + header.getInterleave());
		}

		ByteBuffer bb = ByteBuffer.wrap(data).order(header.getByteOrder());

		FloatBuffer floats = toFloatBuffer(bb, header.getDataType());

		float[] floatData = toBSQ(floats, header);
		return new FloatImage(header, floatData);
	}

	private static void skip(InputStream input, long offset) throws IOException {
		long skipped = 0;
		while (skipped < offset) {
			skipped += input.skip(offset - skipped);
		}

	}

	private static void readStream(InputStream input, int bytes, byte[] dest,
			int offset) throws IOException {
		int bytesRead = 0;
		while (bytesRead < bytes) {
			int tmp = input.read(dest, offset + bytesRead, bytes - bytesRead);
			if (tmp == -1) {
				// EOF
				throw new EnviFormatException("EOF encountered");
			}
			bytesRead += tmp;
		}
	}

	private static float[] toBSQ(FloatBuffer floats, EnviHeader header) {
		switch (header.getInterleave()) {
		case BSQ:
			if (floats.hasArray()) {
				return floats.array();
			} else {
				return new float[header.getDimensions().numElements()];
			}
		case BIL:
			return BILtoBSQ(floats, header.getDimensions());
		case BIP:
			return BIPtoBSQ(floats, header.getDimensions());
		default:
			// will not happen, only 3 types of interleaves are defined
			return null;
		}
	}

	private static float[] BILtoBSQ(FloatBuffer floats, Dimensions dimensions) {
		int bandSize = dimensions.linesSamples();

		float[] data = new float[floats.remaining()];
		for (int line = 0; line < dimensions.numLines; line++) {
			int offsetInBand = line * dimensions.numSamples;
			for (int band = 0; band < dimensions.numBands; band++) {
				floats.get(data, band * bandSize + offsetInBand,
						dimensions.numSamples);
			}
		}
		return data;
	}

	private static float[] BIPtoBSQ(FloatBuffer floats, Dimensions dimensions) {
		int bandSize = dimensions.linesSamples();

		float[] data = new float[floats.remaining()];
		for (int pixel = 0; pixel < bandSize; pixel++) {
			for (int band = 0; band < dimensions.numBands; band++) {
				floats.get(data, band * bandSize + pixel, 1);
			}
		}
		return data;
	}

	private static FloatBuffer toFloatBuffer(ByteBuffer bb, DataType dataType)
			throws UnsupportedEncodingException {
		FloatBuffer fb;

		switch (dataType) {
		case float32:
			fb = bb.asFloatBuffer();
			break;
		case byte8:
			fb = FloatBuffer.wrap(new float[bb.remaining()]);
			while (fb.hasRemaining()) {
				fb.put((float) ((bb.get() & 0xff)));
			}
			break;
		case double64:
			DoubleBuffer db = bb.asDoubleBuffer();
			fb = FloatBuffer.wrap(new float[db.remaining()]);
			while (fb.hasRemaining()) {
				fb.put((float) (db.get()));
			}
			break;
		case int32:
			IntBuffer ib = bb.asIntBuffer();
			fb = FloatBuffer.wrap(new float[ib.remaining()]);
			while (fb.hasRemaining()) {
				fb.put((float) (ib.get()));
			}
			break;
		case long64:
			LongBuffer lb = bb.asLongBuffer();
			fb = FloatBuffer.wrap(new float[lb.remaining()]);
			while (fb.hasRemaining()) {
				fb.put((float) (lb.get()));
			}
			break;
		case short16:
			ShortBuffer sb = bb.asShortBuffer();
			fb = FloatBuffer.wrap(new float[sb.remaining()]);
			while (fb.hasRemaining()) {
				fb.put((float) (sb.get()));
			}
			break;
		case ushort16:
		case ulong32:
		case ulong64:
		case complex2x32:
		case complex2x64:
		default:
			throw new UnsupportedEncodingException("datatype not supported:"
					+ dataType);
		}

		fb.rewind();
		return fb;
	}

	public static void writeHeader(EnviHeader header,
			OutputStream headerOutputStream) throws IOException {
		Writer w = new OutputStreamWriter(headerOutputStream);
		BufferedWriter bw = new BufferedWriter(w);

		Dimensions d = header.getDimensions();

		bw.write("ENVI");
		bw.newLine();
		bw.write("description {");
		bw.newLine();
		bw.write(header.getDescription() + "}");
		bw.newLine();
		bw.write("samples = " + d.numSamples);
		bw.newLine();
		bw.write("lines = " + d.numLines);
		bw.newLine();
		bw.write("bands = " + d.numBands);
		bw.newLine();
		bw.write("data type = " + header.getDataType());
		bw.newLine();
		bw.write("interleave = " + header.getInterleave());
		bw.newLine();
		bw.close();
		w.close();
	}

	public static void writeSignature(EndmemberSet endmembers,
			OutputStream endmemberOutputStream) throws IOException {
		Writer w = new OutputStreamWriter(endmemberOutputStream);
		BufferedWriter bw = new BufferedWriter(w);

		bw.write("SIG");
		bw.newLine();
		bw.write("endmembers = " + endmembers.size());
		bw.newLine();
		bw.write("bands = " + endmembers.bands());
		bw.newLine();
		Endmember[] emArray = endmembers.getEndmembers();
		final int nEndmembers = endmembers.size();
		final int bands = endmembers.bands();
		for (int i = 0; i < nEndmembers; i++) {
			bw.write("=== Endmember " + i + " ===");
			bw.newLine();
			float[] elements = emArray[i].getElements();
			for (int band = 0; band < bands; band++) {
				bw.write(Float.toString(elements[band]));
				bw.newLine();
			}
		}
		bw.close();
		w.close();
	}

	public static BufferedImage getBufferedImage(EnviHeader header,
			InputStream input, int red, int green, int blue) throws IOException {

		// read input from file
		int bytes = header.getDimensions().numElements()
				* header.getDataType().getBytesPerPixel();
		byte[] temp = new byte[bytes];
		readStream(input, bytes, temp, 0);
		ByteBuffer bb = ByteBuffer.wrap(temp).order(header.getByteOrder());
		byte[] data = toBSQ(toBytePixels(bb, header.getDataType()), header);

		// create the band buffers
		int pixels = header.getDimensions().linesSamples();
		ByteBuffer[] bandBuffs = new ByteBuffer[3];
		bandBuffs[0] = ByteBuffer.wrap(data, red * pixels, pixels);
		bandBuffs[1] = ByteBuffer.wrap(data, green * pixels, pixels);
		bandBuffs[2] = ByteBuffer.wrap(data, blue * pixels, pixels);

		// merge the image elements of the band buffers and extract the int[]
		ByteBuffer outBuf = ByteBuffer.allocate(pixels * OUTPUT_FIELDS);
		while (outBuf.hasRemaining()) {
			outBuf.put((byte) 0xFF);
			outBuf.put(bandBuffs[0].get());
			outBuf.put(bandBuffs[1].get());
			outBuf.put(bandBuffs[2].get());
		}
		outBuf.clear();
		int[] pixelArray = new int[pixels];
		outBuf.asIntBuffer().get(pixelArray);

		Dimensions dim = header.getDimensions();
		// setup buffered image
		BufferedImage image = new BufferedImage(dim.numSamples, dim.numLines,
				BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, dim.numSamples, dim.numLines, pixelArray, 0,
				dim.numSamples);
		return image;
	}

	private static byte[] toBSQ(ByteBuffer bytes, EnviHeader header) {
		switch (header.getInterleave()) {
		case BSQ:
			if (bytes.hasArray()) {
				return bytes.array();
			} else {
				return new byte[header.getDimensions().numElements()];
			}
		case BIL:
			return BILtoBSQ(bytes, header.getDimensions());
		case BIP:
			return BIPtoBSQ(bytes, header.getDimensions());
		default:
			// will not happen, only 3 types of interleaves are defined
			return null;
		}
	}

	private static byte[] BILtoBSQ(ByteBuffer bytes, Dimensions dimensions) {
		int bandSize = dimensions.linesSamples();

		byte[] data = new byte[bytes.remaining()];
		for (int line = 0; line < dimensions.numLines; line++) {
			int offsetInBand = line * dimensions.numSamples;
			for (int band = 0; band < dimensions.numBands; band++) {
				bytes.get(data, band * bandSize + offsetInBand,
						dimensions.numSamples);
			}
		}
		return data;
	}

	private static byte[] BIPtoBSQ(ByteBuffer bytes, Dimensions dimensions) {
		int bandSize = dimensions.linesSamples();

		byte[] data = new byte[bytes.remaining()];
		for (int pixel = 0; pixel < bandSize; pixel++) {
			for (int band = 0; band < dimensions.numBands; band++) {
				bytes.get(data, band * bandSize + pixel, 1);
			}
		}
		return data;
	}

	private static ByteBuffer toBytePixels(ByteBuffer source, DataType dataType)
			throws UnsupportedEncodingException {
		ByteBuffer target;

		switch (dataType) {
		case byte8:
			target = source;
			break;
		case float32:
			FloatBuffer fb = source.asFloatBuffer();
			target = ByteBuffer.wrap(new byte[fb.remaining()]);
			while (target.hasRemaining()) {
				target.put((byte) (fb.get() * 255));
			}
			break;
		case double64:
			DoubleBuffer db = source.asDoubleBuffer();
			target = ByteBuffer.wrap(new byte[db.remaining()]);
			while (target.hasRemaining()) {
				target.put((byte) (db.get() * 255));
			}
			break;
		case int32:
			IntBuffer ib = source.asIntBuffer();
			target = ByteBuffer.wrap(new byte[ib.remaining()]);
			while (target.hasRemaining()) {
				target.put((byte) ((ib.get() >> 24) + 128));
			}
			break;
		case long64:
			LongBuffer lb = source.asLongBuffer();
			target = ByteBuffer.wrap(new byte[lb.remaining()]);
			while (target.hasRemaining()) {
				target.put((byte) ((lb.get() >> 56) + 128));
			}
			break;
		case short16:
			ShortBuffer sb = source.asShortBuffer();
			target = ByteBuffer.wrap(new byte[sb.remaining()]);
			while (target.hasRemaining()) {
				target.put((byte) ((sb.get() >> 8) + 128));
			}
			break;
		case ushort16:
		case ulong32:
		case ulong64:
		case complex2x32:
		case complex2x64:
		default:
			throw new UnsupportedEncodingException("datatype not supported:"
					+ dataType);
		}

		target.rewind();
		return target;
	}

}
