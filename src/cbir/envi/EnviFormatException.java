package cbir.envi;


import java.io.IOException;

public class EnviFormatException extends IOException {

    private static final long serialVersionUID = 1L;

    public EnviFormatException() {
        super();
    }

    public EnviFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnviFormatException(String message) {
        super(message);
    }

    public EnviFormatException(Throwable cause) {
        super(cause);
    }

}
