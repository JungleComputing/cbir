package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.MatchTable;

public class QueryResultEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1952308880530180592L;
	private final long queryTimeStamp;

	public QueryResultEvent(ActivityIdentifier source,
			ActivityIdentifier target, long queryTimeStamp, MatchTable[] results) {
		super(source, target, results);
		this.queryTimeStamp = queryTimeStamp;
	}

	public MatchTable[] getResults() {
		return (MatchTable[]) super.data;
	}

    public long getQueryTimeStamp() {
        return queryTimeStamp;
    }
	
}
