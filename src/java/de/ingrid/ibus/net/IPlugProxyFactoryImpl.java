/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus.net;

import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.weta.components.communication.ICommunication;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;

/**
 * 
 */
public class IPlugProxyFactoryImpl implements IPlugProxyFactory {

    private ICommunication fCommunication;

    private Log fLogger = LogFactory.getLog(this.getClass());

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
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.iplug.PlugDescription)
     */
    public IPlug createPlugProxy(PlugDescription plug) throws Exception {
        IPlug result = null;

        final String wetagUrl = plug.getProxyServiceURL();
        final Class iPlugClass = Thread.currentThread().getContextClassLoader().loadClass(plug.getIPlugClass());

        ProxyService proxyService = new ProxyService();
        proxyService.setCommunication(this.fCommunication);
        RemoteInvocationController ric = null;
        try {
            ric = proxyService.createRemoteInvocationController(wetagUrl);
            result = (IPlug) ric.newInstance(iPlugClass, null, null);
            result.configure(plug);
        } catch (ConnectException e) {
            throw e;
        } catch (Throwable t) {
            this.fLogger.error(t.getMessage(), t);
        }

        return result;
    }
}
