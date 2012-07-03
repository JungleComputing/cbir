//package cbir.node;
//
//import ibis.constellation.Executor;
//import ibis.constellation.SingleEventCollector;
//import ibis.constellation.context.UnitActivityContext;
//
//import java.net.URISyntaxException;
//import java.util.Arrays;
//
//import org.gridlab.gat.URI;
//
//import cbir.Cbir;
//import cbir.Config;
//import cbir.MatchTable;
//import cbir.backend.MultiArchiveIndex;
//import cbir.backend.activities.CalculateEndmembers;
//import cbir.frontend.QueryInitiator;
//import cbir.frontend.activities.GetImageCollection;
//import cbir.frontend.activities.LaunchQuery;
//import cbir.metadata.Metadata;
//import cbir.vars.ContextStrings;
//
///**
// * @author Timo van Kessel
// * 
// */
//public class UINode extends Node {
//
//	private String[] stores;
//
//	public UINode(String[] stores, Executor... executors) {
//		super(executors);
//		this.stores = stores;
//	}
//
//	private String[] getStores() {
//		return stores;
//	}
//
//	private MatchTable[] query(String query, MultiArchiveIndex searchScope) {
//		Metadata md = getMetadata(query);
//		System.out.println("Got Query Metadata!");
//
//		LaunchQuery q = new LaunchQuery(md, searchScope, Config.nResults,
//				Config.batchSize);
//		submit(q);
//		System.out.println("Query Started...");
//		return q.waitForResults();
//	}
//	
//	private MatchTable[] query(Metadata queryData, MultiArchiveIndex searchScope) {
//		LaunchQuery q = new LaunchQuery(queryData, searchScope, Config.nResults,
//				Config.batchSize);
//		submit(q);
//		System.out.println("Query Started...");
//		return q.waitForResults();
//	}
//	
//	private Metadata getMetadata(String imageURI) {
//		SingleEventCollector querySec = new SingleEventCollector(
//				new UnitActivityContext(ContextStrings.QUERY_INITIATOR));
//		submit(new CalculateEndmembers(imageURI,
//				Config.QUERY_INITIATOR_REPOSITORY, submit(querySec)));
//
//		return (Metadata) querySec.waitForEvent().data;
//	}
//
//	private MultiArchiveIndex getSearchSpace() {
//		SingleEventCollector scopeSec = new SingleEventCollector(
//				new UnitActivityContext(ContextStrings.QUERY_INITIATOR));
//		submit(new GetImageCollection(submit(scopeSec), stores));
//		MultiArchiveIndex searchScope = (MultiArchiveIndex) scopeSec
//				.waitForEvent().data;
//		System.out.println("Got SearchScope: " + searchScope.size()
//				+ " images!");
//		return searchScope;
//
//	}
//	
//	private static void printResults(int results, MatchTable[] tables) {
//		int resultSize = Math.min(results, tables.length);
//		for (int i = 0; i < resultSize; i++) {
//			if (tables[i] == null) {
//				break;
//			}
//			System.out.println(String.format("%2d) %s: %f", i,
//					tables[i].referenceName(),
//					tables[i].getScore()));
//		}
//	}
//
//	public static void main(String[] args) throws URISyntaxException {
//		int arg = 0;
//		String baseURI = args[arg];
//		arg++;
//		int length = Integer.parseInt(args[arg]);
//		arg++;
//		String[] stores = Arrays.copyOfRange(args, arg, length + arg);
//		arg += length;
//		length = Integer.parseInt(args[arg]);
//		arg++;
//		String[] queries = Arrays.copyOfRange(args, arg, length + arg);
//		arg += length;
//		
//		long deadline = System.currentTimeMillis() + Long.parseLong(args[arg]) * 60000;
//		arg++;
//
//		System.out.println("Initializing Cbir");
//		System.out.println("stores:");
//		for (String store : stores) {
//			System.out.println(store);
//		}
//		System.out.println("---");
//		Cbir cbir = new Cbir();
//		System.out.println("Initializing QueryInititator");
//		QueryInitiator qi = cbir.getFactory().createQueryInitiator(
//				new URI(baseURI), stores, true, false);
//		System.out.println("Creating UINode");
//		UINode node = new UINode(stores, qi);
//		System.out.println("Activating UINode");
//		node.activate();
//		System.out.println("Interface started");
//		
//		Metadata[] queryData = new Metadata[queries.length];
//		for(int i = 0; i < queries.length; i++) {
//			queryData[i] = node.getMetadata(queries[i]);
//		}
//
//		boolean done = false;
//		while (!done) {
//			MatchTable[] tables = null;
//			MultiArchiveIndex searchSpace = node.getSearchSpace();
////			for (String query : queries) {
////				long time = System.nanoTime();
////				tables = node.query(query, searchSpace);
////				time = System.nanoTime() - time;
////				System.out.println(String.format("Query for %s took %d millis",
////						query, time / 1000000));
////			}
//			for (Metadata query : queryData) {
//				long time = System.nanoTime();
//				tables = node.query(query, searchSpace);
//				time = System.nanoTime() - time;
//				System.out.println(String.format("Query for %s took %d millis",
//						query.getUUID(), time / 1000000));
//			}
//			System.out.println("queries finished");
//			printResults(20, tables);
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// ignore
//			}
//			if(System.currentTimeMillis() > deadline) {
//				done = true;
//			}
//		}
//
//		node.done();
//	}
//
//}
