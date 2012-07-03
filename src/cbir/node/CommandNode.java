package cbir.node;

import ibis.constellation.Executor;

import java.net.URISyntaxException;
import java.util.Arrays;

import org.gridlab.gat.URI;

import cbir.Cbir;
import cbir.MatchTable;
import cbir.frontend.QueryInitiator;

/**
 * @author Timo van Kessel
 * 
 */
public class CommandNode extends Node {

	public CommandNode(String[] stores, Executor... executors) {
		super(executors);
	}

	private static void printResults(int results, MatchTable[] tables) {
		int resultSize = Math.min(results, tables.length);
		for (int i = 0; i < resultSize; i++) {
			if (tables[i] == null) {
				break;
			}
			System.out.println(String.format("%2d) %s: %f", i,
					tables[i].referenceImageID(),
					tables[i].getScore()));
		}
	}



	public static void main(String[] args) throws URISyntaxException {
		int arg = 0;
		String baseURI = args[arg];
		arg++;
		int length = Integer.parseInt(args[arg]);
		arg++;
		String[] stores = Arrays.copyOfRange(args, arg, length + arg);
		arg += length;
		
		System.out.println("Initializing Cbir");
		System.out.println("stores:");
		for (String store : stores) {
			System.out.println(store);
		}
		System.out.println("---");
		Cbir cbir = new Cbir();
		
		System.out.println("Initializing QueryInitiator");
		QueryInitiator qi = cbir.getFactory().createQueryInitiator(
				new URI(baseURI), stores, true, true);
		System.out.println("Creating CommandNode");
		CommandNode node = new CommandNode(stores, qi);
		System.out.println("Activating CommandNode");
		node.activate();
		System.out.println("CommandNode started");
		
		node.done();
	}

}
