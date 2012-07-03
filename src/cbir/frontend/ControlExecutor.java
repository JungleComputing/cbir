package cbir.frontend;

import ibis.constellation.SimpleExecutor;
import ibis.constellation.StealPool;
import ibis.constellation.WorkerContext;

public class ControlExecutor extends SimpleExecutor {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6982117775122377759L;
	
	public ControlExecutor(StealPool belongsTo, StealPool stealsFrom, WorkerContext context) {
		super(belongsTo, stealsFrom, context);
	}
}
