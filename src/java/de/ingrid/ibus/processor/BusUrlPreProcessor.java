package de.ingrid.ibus.processor;

import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.IngridQuery;

public class BusUrlPreProcessor implements IPreProcessor {

    public static final String BUS_URL = "BUS_URL";

    private final String _busUrl;

    public BusUrlPreProcessor(String busUrl) {
        _busUrl = busUrl;
    }

    @Override
    public void process(IngridQuery query) throws Exception {
        query.put(BUS_URL, _busUrl);
    }

}
