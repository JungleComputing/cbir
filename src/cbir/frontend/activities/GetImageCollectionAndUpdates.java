package cbir.frontend.activities;

import ibis.constellation.Activity;
import ibis.constellation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cbir.backend.SingleArchiveIndex;
import cbir.backend.store.StoreIndexMessage;
import cbir.frontend.QueryInitiator;
import cbir.vars.CBIRActivityContext;
import cbir.vars.ContextStrings;

public class GetImageCollectionAndUpdates extends Activity {

    private static final long serialVersionUID = -3375885765826899357L;

    private static final Logger logger = LoggerFactory
            .getLogger(GetImageCollectionAndUpdates.class);

    private String[] stores;
    private int expectedIndices;

    public GetImageCollectionAndUpdates(String... stores) {
        super(new CBIRActivityContext(ContextStrings.QUERY_INITIATOR, true),
                true, true);
        this.stores = stores;
    }

    @Override
    public QueryInitiator getExecutor() {
        return (QueryInitiator) super.getExecutor();
    }

    @Override
    public void initialize() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("INIT");
        }
        if (stores == null || stores.length == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("No stores --> done");
            }
            finish();
        } else {
            expectedIndices = stores.length;
            for (String store : stores) {
                getExecutor().submit(
                        new GetStoreIndexAndRegisterForUpdates(store,
                                identifier()));
            }

            suspend();
        }
    }

    @Override
    public void process(Event e) throws Exception {
        if (e instanceof StoreIndexMessage) {
            SingleArchiveIndex sai = ((StoreIndexMessage) e).getIndex(); 
            if(sai.size() >0) {
                getExecutor().addIndex(sai, getExecutor());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Got storeIndexMessage "
                        + sai.getArchiveName() + ", size = " + sai.size());
            }
            suspend();
        } else {
            logger.warn("Received an unknown event: " + e.toString());
            suspend();
        }

    }

    @Override
    public void cleanup() throws Exception {
        // empty
    }

    @Override
    public void cancel() throws Exception {
        // empty

    }

}
