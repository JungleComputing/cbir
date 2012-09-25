package cbir;

public class Config {

    public static final int EXTRACTION_ATTEMPTS = 3;

    public static final String QUERY_INITIATOR_REPOSITORY = "QUERY_INITIATOR";
    public static int nPrincipalComponents = 20;
    public static final boolean spcaFixedNumIterations = false;
    public static int spcaIterations = 400;
    public static boolean nFindrRandomValues = true;
    public static String nFindrInitFile = null;
    public static boolean spcaGenerate = true;
    public static String spcaVectorFile = null;

    public static int batchSize = 40;
    public static int nResults = 30;

    public static final int GUI_WIDTH = 1060;
    public static final int GUI_HEIGHT = 768;

}
