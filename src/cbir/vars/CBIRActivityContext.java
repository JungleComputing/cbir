package cbir.vars;

import ibis.constellation.context.UnitActivityContext;

public class CBIRActivityContext extends UnitActivityContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7889628664363892091L;

	public CBIRActivityContext(String name, boolean interactive) {
		super(name, interactive? 2: 1);
	}
	
}
