/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

package de.ingrid.ibus.comm.net;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * For implementing a factory for creation of IPlug proxies with the bus.
 */
public interface IPlugProxyFactory {

    /**
     * Creates a IPlug proxy from its description.
     * @param plug The descrption of a plug.
     * @param busurl The url the plug is connected to.
     * @return The created IPlug proxy instance.
     * @throws Exception If the plug cannot be created.
     */
    public IPlug createPlugProxy(PlugDescription plug, String busurl) throws Exception;

}
