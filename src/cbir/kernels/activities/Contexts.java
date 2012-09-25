package cbir.kernels.activities;


import ibis.constellation.ActivityContext;
import ibis.constellation.context.OrActivityContext;
import ibis.constellation.context.UnitActivityContext;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;
import cbir.vars.Ranks;

public class Contexts {

	public static final ActivityContext featureExtraction = new OrActivityContext(
			new UnitActivityContext[] {
					new CBIRActivityContext(ContextStrings.GPU_KERNEL,
							false),
					new CBIRActivityContext(ContextStrings.CPU_KERNEL,
							false) }, true);
	
	public static final ActivityContext endmemberExtraction = new CBIRActivityContext(
			ContextStrings.CPU_KERNEL, false);

	public static final ActivityContext lsu = new OrActivityContext(
			new UnitActivityContext[] {
					new CBIRActivityContext(ContextStrings.GPU_KERNEL,
							false),
					new CBIRActivityContext(ContextStrings.CPU_KERNEL,
							false) }, true);

	public static final ActivityContext matching = new CBIRActivityContext(
			ContextStrings.CPU_KERNEL, true);

	public static final ActivityContext nFindr = new OrActivityContext(
			new UnitActivityContext[] {
					new CBIRActivityContext(ContextStrings.GPU_KERNEL,
							false),
					new CBIRActivityContext(ContextStrings.CPU_KERNEL,
							false) }, true);

	public static final ActivityContext pca = new CBIRActivityContext(
			ContextStrings.CPU_KERNEL, false);

	public static final ActivityContext spca = new OrActivityContext(
			new UnitActivityContext[] {
					new CBIRActivityContext(ContextStrings.GPU_KERNEL,
							false),
					new CBIRActivityContext(ContextStrings.CPU_KERNEL,
							false) }, true);
}
