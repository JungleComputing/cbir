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

public abstract class KernelExecutor extends SimpleExecutor {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5673441851188471775L;

    // static KernelExecutor create(StealPool belongsTo, StealPool stealsFrom) {
    // WorkerContext context;
    //
    // context = new UnitWorkerContext(ContextStrings.CPU_KERNEL);
    // // contexts[1] = UnitWorkerContext.DEFAULT;
    //
    // return new KernelExecutor(belongsTo, stealsFrom, context);
    // }
    //
    // static KernelExecutor create(StealPool belongsTo, StealPool stealsFrom,
    // long cudaHandle) {
    // WorkerContext context;
    //
    // UnitWorkerContext[] contexts = new UnitWorkerContext[2];
    // contexts[0] = new UnitWorkerContext(ContextStrings.GPU_KERNEL);
    // contexts[1] = new UnitWorkerContext(ContextStrings.CPU_KERNEL);
    // // contexts[2] = UnitWorkerContext.DEFAULT;
    // context = new OrWorkerContext(contexts, true);
    //
    // return new KernelExecutor(belongsTo, stealsFrom, context, cudaHandle);
    // }

    private Kernels kernels;
    
    
    // logging variables
    

    protected KernelExecutor(StealPool belongsTo, StealPool stealsFrom,
            WorkerContext context) {
        super(belongsTo, stealsFrom, context, StealStrategy.SMALLEST,
                StealStrategy.BIGGEST, StealStrategy.BIGGEST);
        kernels = Kernels.getKernels(true, true);
    }

    protected KernelExecutor(StealPool belongsTo, StealPool stealsFrom,
            WorkerContext context, long cudaHandle) {
        super(belongsTo, stealsFrom, context, StealStrategy.SMALLEST,
                StealStrategy.BIGGEST, StealStrategy.BIGGEST);
        kernels = Kernels.getKernels(true, true, cudaHandle);
    }

    public Kernels getKernels() {
        return kernels;
    }

    
    
    protected abstract void printStatistics();

    private void printKernelStatistics() {
        StringBuilder sb = new StringBuilder("KernelExecutor: " + identifier()
                + "\n");
        sb.append("Context: " + getContext() + "\n");
        sb.append("Match         : " + kernels.matchTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.matchTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.matchAvgTime()/1000 + " us.\n");
        sb.append("LSU           : " + kernels.lsuTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.lsuTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.lsuAvgTime()/1000 + " us.\n");
        sb.append("PCA           : " + kernels.pcaTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.pcaTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.pcaAvgTime()/1000 + " us.\n");
        sb.append("SPCA          : " + kernels.spcaTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.spcaTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.spcaAvgTime()/1000 + " us.\n");
        sb.append("NFindr        : " + kernels.nFindrTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.nFindrTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.nFindrAvgTime()/1000 + " us.\n");
        sb.append("Extraction    : " + kernels.endExTasks() + " invocations.\n");
        sb.append("   total time : " + kernels.endExTime()/1000000 + " ms.\n");
        sb.append("   avg time   : " + kernels.endExAvgTime()/1000 + " us.\n");
        sb.append("--------------------------");

        System.out.println(sb.toString());
    }

    @Override
    public void run() {
        super.run();
        printKernelStatistics();
        printStatistics();
    }
}
