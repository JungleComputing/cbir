//package cbir.events;
//
//import ibis.constellation.ActivityIdentifier;
//import ibis.constellation.Event;
//import cbir.backend.MultiArchiveIndex;
//
//public class StoreIndexEvent extends Event {
//
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 49225409224367393L;
//
//	public StoreIndexEvent(ActivityIdentifier source,
//			ActivityIdentifier target, MultiArchiveIndex index) {
//		super(source, target, index);
//	}
//
//	public MultiArchiveIndex getIndex() {
//		return (MultiArchiveIndex) super.data;
//	}
//	
//}
