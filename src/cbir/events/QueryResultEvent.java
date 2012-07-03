package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.MatchTable;

public class QueryResultEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1952308880530180592L;

	public QueryResultEvent(ActivityIdentifier source,
			ActivityIdentifier target, MatchTable[] results) {
		super(source, target, results);
	}

	public MatchTable[] getResults() {
		return (MatchTable[]) super.data;
	}
	
}
