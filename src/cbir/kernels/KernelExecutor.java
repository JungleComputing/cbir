package cbir.kernels;

import ibis.constellation.SimpleExecutor;
import ibis.constellation.StealPool;
import ibis.constellation.StealStrategy;
import ibis.constellation.WorkerContext;
import ibis.constellation.context.OrWorkerContext;
import ibis.constellation.context.UnitWorkerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.kernels.cuda.CudaKernel;
import cbir.vars.ContextStrings;

public class KernelExecutor extends SimpleExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5673441851188471775L;
	
	static KernelExecutor create(StealPool belongsTo, StealPool stealsFrom, boolean allowGPU) {
		WorkerContext context;
		final boolean useGPU = allowGPU && CudaKernel.available();
		
		if(useGPU) {
			UnitWorkerContext[] contexts = new UnitWorkerContext[2];
			contexts[0] = new UnitWorkerContext(ContextStrings.GPU_KERNEL); 
			contexts[1] = new UnitWorkerContext(ContextStrings.CPU_KERNEL);
//			contexts[2] = UnitWorkerContext.DEFAULT;
			context = new OrWorkerContext(contexts, true);
			
		} else {
			context = new UnitWorkerContext(ContextStrings.CPU_KERNEL);
//			contexts[1] = UnitWorkerContext.DEFAULT;
		}
		
		
		return new KernelExecutor(belongsTo, stealsFrom, context, useGPU);
	}
	
	
	private Kernels kernels;
	
	protected KernelExecutor(StealPool belongsTo, StealPool stealsFrom, WorkerContext context, boolean useGPU) {
		super(belongsTo, stealsFrom, context,
	              StealStrategy.SMALLEST, 
	              (useGPU ? StealStrategy.BIGGEST : StealStrategy.BIGGEST),
	              StealStrategy.BIGGEST);
		kernels = Kernels.getKernels(true, true, useGPU);		
	}
	
	public Kernels getKernels() {
		return kernels;
	}
}
