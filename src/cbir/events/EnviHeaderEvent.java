package cbir.events;

import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Event;
import cbir.envi.EnviHeader;

public class EnviHeaderEvent extends Event {

    /**
     * 
     */
    private static final long serialVersionUID = -5725060888867638750L;

    public EnviHeaderEvent(ActivityIdentifier source,
            ActivityIdentifier target, EnviHeader header) {
        super(source, target, header);
    }

    public EnviHeader getHeader() {
        return (EnviHeader) super.data;
    }

}
