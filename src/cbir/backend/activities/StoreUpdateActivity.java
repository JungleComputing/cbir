//package cbir.backend.activities;
//
//import ibis.constellation.Event;
//
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import cbir.backend.MultiArchiveIndex;
//import cbir.backend.SingleArchiveIndex;
//import cbir.kernels.activities.CalculateEndmembers;
//import cbir.metadata.Metadata;
//
//public class StoreUpdateActivity extends StoreActivity {
//
//	private static final Logger logger = LoggerFactory
//			.getLogger(StoreUpdateActivity.class);
//
//	private static final long serialVersionUID = -3375885765826899357L;
//
//	private final String[] repositories;
//	private int receivedMetadata = 0;
//
//	private int receivedRepositoryContents = 0;
//
//	private MultiArchiveIndex mai = null;
//
//	private StoreUpdateActivity(String storeName, String... repositories) {
//		super(false, true, storeName);
//		this.repositories = repositories;
//		if (logger.isDebugEnabled()) {
//			logger.debug("CONSTRUCTOR");
//			logger.debug("Context: " + getContext());
//		}
//		
//	}
//
//	@Override
//	public void initialize() throws Exception {
//		if (logger.isDebugEnabled()) {
//			logger.debug("INIT " + identifier());
//		}
//		mai = new MultiArchiveIndex();
//
//		for (String repository : repositories) {
//			getExecutor().submit(
//					new GetRepositoryContents(repository, identifier()));
//		}
//
//		if (logger.isDebugEnabled()) {
//			logger.debug("Retrieving RepositoryContents...");
//		}
//
//		suspend();
//	}
//
//	@Override
//	public void process(Event e) throws Exception {
//		if (e.data instanceof Metadata) {
//			Metadata md = (Metadata) e.data;
//			getStore().put(md, mai.getStoresFor(md.getUUID()));
//			receivedMetadata++;
//			if (logger.isDebugEnabled()) {
//				logger.debug("Received metadata for "
//						+ ((Metadata) e.data).getUUID());
//				logger.debug("Store now contains " + getStore().size() + " images");
//			}
//			if (mai.size() == receivedMetadata) {
//				if (logger.isDebugEnabled()) {
//					logger.debug("Acquired all metadata");
//				}
//				finish();
//			} else {
//				suspend();
//			}
//		} else if (e.data instanceof SingleArchiveIndex) {
//			// merge repositoryIndices
//
//			SingleArchiveIndex sai = (SingleArchiveIndex) e.data;
//			mai.add(sai);
//
//			receivedRepositoryContents++;
//			if (receivedRepositoryContents == repositories.length) {
//				if (logger.isDebugEnabled()) {
//					logger.debug("... got RepositoryContents, start creation of metadata...");
//				}
//				startFetchMetadata();
//			}
//			suspend();
//		} else {
//			if (logger.isDebugEnabled()) {
//				logger.debug("Received an unsupported event:" + e.toString());
//			}
//			suspend();
//		}
//	}
//
//	private void startFetchMetadata() {
//		// start fetching metadata for all images
//		for (Entry<String, Set<String>> element : mai.getUUIDIndex().entrySet()) {
//			getExecutor().submit(
//					new CalculateEndmembers(element.getKey(), element
//							.getValue().toArray(new String[0]), identifier()));
//		}
//	}
//
//	@Override
//	public void cleanup() throws Exception {
//		// empty
//
//	}
//
//	@Override
//	public void cancel() throws Exception {
//		// empty
//
//	}
//
//}
