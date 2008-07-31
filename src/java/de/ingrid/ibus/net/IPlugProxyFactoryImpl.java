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
 * IPlugProxyFactory implementation that uses the weta communication interface. Currently there is one with pure java
 * sockets and one with jxta. The jxta one is used in the ingrid project.
 * 
 * @see net.weta.components.communication.ICommunication
 */
public class IPlugProxyFactoryImpl implements IPlugProxyFactory {

    private ICommunication fCommunication;

    /**
     * This constructor does nothing and is only implemented for serialization.
     */
    public IPlugProxyFactoryImpl() {
        // for serialization
    }

    /**
     * This constructor sets the communication object that implements net.weta.components.communication.ICommunication.
     * 
     * @param communication
     *            The object for communication.
     */
    public IPlugProxyFactoryImpl(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription, String)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription, String busurl) throws Exception {
        String plugUrl = plugDescription.getProxyServiceURL();
        if (plugDescription.isRecordloader()) {
            return (IPlug) ProxyService.createProxy(this.fCommunication,
                    new Class[] { IPlug.class, IRecordLoader.class }, plugUrl);
        }
        return (IPlug) ProxyService.createProxy(this.fCommunication, IPlug.class, plugUrl);
    }
}
