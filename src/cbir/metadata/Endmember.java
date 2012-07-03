package cbir.metadata;

import java.io.Serializable;

public class Endmember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4742113911206938432L;
	private float[] elements;
	
	public Endmember(float[] elements) {
		this.elements = elements;
	}
	
	public float[] getElements() {
		return elements;
	}

	public int bands() {
		return elements.length;
	}
}
