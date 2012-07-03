package cbir.backend.repository.operations;
//package cbir.repository.operations;
//
//import ibis.constellation.ActivityContext;
//import ibis.constellation.context.UnitActivityContext;
//
//import java.nio.ByteBuffer;
//
//import cbir.envi.Dimensions;
//import cbir.rts.ContextStrings;
//import cbir.rts.Ranks;
//
//
//public interface IOEnvi {
//	
//	public static final ActivityContext context = new  UnitActivityContext(ContextStrings.HYPERSPECTRAL_CPU, Ranks.DEFAULT);
//	
//	Dimensions readHeader(String header);
//	
//	ByteBuffer loadData(String fileName, Dimensions dimensions);
//	
//	void writeImage(float[] image, String fileName, int numLines, int numSamples, int numBands);
//}
