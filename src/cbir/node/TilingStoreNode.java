package cbir.node;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Executor;

import java.util.Arrays;

import cbir.Cbir;
import cbir.backend.MetadataStore;
import cbir.backend.activities.StoreTiledUpdateActivity;
import cbir.backend.store.MetadataStoreImpl;

public class TilingStoreNode extends Node {

	private final String[] repositories;
	private final String storeName;
	private final int tileWidth, tileHeight;

	public TilingStoreNode(String storeName, int tileWidth, int tileHeight,
			String[] repositories, Executor... executors) {
		super(executors);
		this.storeName = storeName;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.repositories = repositories;

	}

	@Override
	public void activate() {
		super.activate();
		ActivityIdentifier id = submit(new StoreTiledUpdateActivity(storeName,
				tileWidth, tileHeight, repositories));
		System.out.println("StoreTiledUpdateActivity: " + id + " submitted");

	}

	public static void main(String[] args) {
		String[] repositories;
		String storeName;

		int executors = Integer.parseInt(args[0]);
		storeName = args[1];
		int width = Integer.parseInt(args[2]);
		int height = Integer.parseInt(args[3]);
		repositories = Arrays.copyOfRange(args, 4, args.length);

		Cbir cbir = new Cbir();

		if(executors < 1) {
			executors = 1;
		}
		MetadataStore store = new MetadataStoreImpl(storeName);
		Executor[] execs = new Executor[executors];
		for(int i = 0; i < executors; i++) {
			execs[i] = cbir.getFactory().createMetadataStoreExecutor(store,
				repositories);
		}
		

		TilingStoreNode node = new TilingStoreNode(storeName, width, height, repositories, execs);
		store.enableUpdates(node);
		node.activate();
		

		// nothing to do here, just wait for completion...

		node.done();
	}
}
