/*
 * **************************************************-
 * Ingrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 */
public class DummyProxyFactory implements IPlugProxyFactory {
    private float[][] useScoresPerIPlug = null;
    
    private int numCreatedIPlugs = 0;
    
    public DummyProxyFactory() {}
    
    public DummyProxyFactory(float[][] scores) {
        this.useScoresPerIPlug = scores;
    }
    
    /**
     * @throws Exception 
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription, java.lang.String)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription, String busurl) throws Exception {
        IPlug plug;
        if (useScoresPerIPlug != null)
            plug = new DummyIPlug(plugDescription.getPlugId(), useScoresPerIPlug[numCreatedIPlugs++]);
        else
            plug = new DummyIPlug(plugDescription.getPlugId());
        plug.configure(plugDescription);
        
        return plug;
    }

}
