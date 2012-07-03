package cbir.kernels.activities;


import ibis.constellation.ActivityContext;
import ibis.constellation.context.OrActivityContext;
import ibis.constellation.context.UnitActivityContext;
import cbir.vars.ContextStrings;
import cbir.vars.Ranks;

public class Contexts {

	public static final ActivityContext featureExtraction = new OrActivityContext(
			new UnitActivityContext[] {
					new UnitActivityContext(ContextStrings.GPU_KERNEL,
							Ranks.DEFAULT),
					new UnitActivityContext(ContextStrings.CPU_KERNEL,
							Ranks.DEFAULT) }, true);
	
	public static final ActivityContext endmemberExtraction = new UnitActivityContext(
			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);

	public static final ActivityContext lsu = new OrActivityContext(
			new UnitActivityContext[] {
					new UnitActivityContext(ContextStrings.GPU_KERNEL,
							Ranks.DEFAULT),
					new UnitActivityContext(ContextStrings.CPU_KERNEL,
							Ranks.DEFAULT) }, true);

	public static final ActivityContext matching = new UnitActivityContext(
			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);

	public static final ActivityContext nFindr = new OrActivityContext(
			new UnitActivityContext[] {
					new UnitActivityContext(ContextStrings.GPU_KERNEL,
							Ranks.DEFAULT),
					new UnitActivityContext(ContextStrings.CPU_KERNEL,
							Ranks.DEFAULT) }, true);

	public static final ActivityContext pca = new UnitActivityContext(
			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);

	public static final ActivityContext spca = new OrActivityContext(
			new UnitActivityContext[] {
					new UnitActivityContext(ContextStrings.GPU_KERNEL,
							Ranks.DEFAULT),
					new UnitActivityContext(ContextStrings.CPU_KERNEL,
							Ranks.DEFAULT) }, true);
}
