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
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription) throws Exception {
        final String plugUrl = plugDescription.getProxyServiceURL();

        if (plugDescription.isRecordloader()) {
            return (IPlug) ProxyService.createProxy(this.fCommunication, IPlug.class, plugUrl);
        } else {
            return (IPlug) ProxyService.createProxy(this.fCommunication, new Class[] { IPlug.class, IRecordLoader.class },
                    plugUrl);
        }

    }
}
