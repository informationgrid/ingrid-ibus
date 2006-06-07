/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus.net;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ProxyService;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.PlugDescription;

/**
 * 
 */
public class IPlugProxyFactoryImpl implements IPlugProxyFactory {

    private ICommunication fCommunication;

    /**
     */
    public IPlugProxyFactoryImpl() {
        // for serialization
    }

    /**
     * @param communication
     */
    public IPlugProxyFactoryImpl(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription, String)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription, String busurl) throws Exception {
        String plugUrl = null;
        if (busurl != null) {
            final String path = busurl.replaceFirst("/(.*)?:(.*)?", "$1");
            final String peername = plugDescription.getProxyServiceURL().replaceFirst("/(.*)?:(.*)?", "$2");
            plugUrl = "/" + path + ":" + peername;
        } else {
            plugUrl = plugDescription.getProxyServiceURL();
        }

        if (plugDescription.isRecordloader()) {
            return (IPlug) ProxyService.createProxy(this.fCommunication,
                    new Class[] { IPlug.class, IRecordLoader.class }, plugUrl);
        }
        return (IPlug) ProxyService.createProxy(this.fCommunication, IPlug.class, plugUrl);
    }
}
