package cbir.backend.store;

import cbir.metadata.Metadata;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;

public class MetadataMessage extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5075103484480468299L;

	public MetadataMessage(ActivityIdentifier source, ActivityIdentifier target,
			Metadata data) {
		super(source, target, data);
	}

	public Metadata getMetadata() {
		return (Metadata)data;
	}
}
