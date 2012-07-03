package cbir;

public class Cbir {
	private final ExecutorFactory factory;
	
	public Cbir() {
		factory = new ExecutorFactory();
	}
	
	public ExecutorFactory getFactory() {
		return factory;
	}
	
	
}
