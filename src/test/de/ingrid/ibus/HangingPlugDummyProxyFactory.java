/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

public class HangingPlugDummyProxyFactory implements IPlugProxyFactory {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IPlug createPlugProxy(PlugDescription plug) throws Exception {
        return new HangingPlug();
    }

    class HangingPlug implements IPlug {

        public void configure(PlugDescription plugDescription) throws Exception {
            // TODO Auto-generated method stub

        }

        public IngridHits search(IngridQuery query, int start, int length) throws Exception {
            System.out.println("i will wait for ever");
            int i =0;
            while (true){
            }
                

        }

        public IngridHitDetail getDetails(IngridHit hit, IngridQuery query) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
