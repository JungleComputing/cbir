//package cbir.vars;
//
//import ibis.constellation.ActivityContext;
//import ibis.constellation.WorkerContext;
//import ibis.constellation.context.OrActivityContext;
//import ibis.constellation.context.UnitActivityContext;
//import ibis.constellation.context.UnitWorkerContext;
//
//public class Contexts {
//
//	public static final ActivityContext endmemberExtraction = new UnitActivityContext(
//			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);
//
//	public static final ActivityContext lsu = new OrActivityContext(
//			new UnitActivityContext[] {
//					new UnitActivityContext(ContextStrings.GPU_KERNEL,
//							Ranks.DEFAULT),
//					new UnitActivityContext(ContextStrings.CPU_KERNEL,
//							Ranks.DEFAULT) }, true);
//
//	public static final ActivityContext matching = new UnitActivityContext(
//			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);
//
//	public static final ActivityContext nFindr = new OrActivityContext(
//			new UnitActivityContext[] {
//					new UnitActivityContext(ContextStrings.GPU_KERNEL,
//							Ranks.DEFAULT),
//					new UnitActivityContext(ContextStrings.CPU_KERNEL,
//							Ranks.DEFAULT) }, true);
//
//	public static final ActivityContext pca = new UnitActivityContext(
//			ContextStrings.CPU_KERNEL, Ranks.DEFAULT);
//
//	public static final ActivityContext spca = new OrActivityContext(
//			new UnitActivityContext[] {
//					new UnitActivityContext(ContextStrings.GPU_KERNEL,
//							Ranks.DEFAULT),
//					new UnitActivityContext(ContextStrings.CPU_KERNEL,
//							Ranks.DEFAULT) }, true);
//	
//	
//	
//	
//	
//	public static ActivityContext createForStore(String storeName) {
//		return new UnitActivityContext(ContextStrings.createForStore(storeName));
//	}
//	
//	public static ActivityContext createForRepository(String repositoryName) {
//		return new UnitActivityContext(ContextStrings.createForRepository(repositoryName));
//	}
//	
//	public static ActivityContext createForRepositoryMaster(String repositoryName) {
//		return new UnitActivityContext(ContextStrings.createForRepositoryMaster(repositoryName));
//	}
//	
//	public static WorkerContext createForQuery(String storeName) {
//		return new UnitWorkerContext(ContextStrings.createForStore(storeName));
//	}
//		
//}
