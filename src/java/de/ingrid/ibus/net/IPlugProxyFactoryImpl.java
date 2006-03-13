/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus.net;

import java.net.ConnectException;

import net.weta.components.communication.ICommunication;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 */
public class IPlugProxyFactoryImpl implements IPlugProxyFactory {

    private Log fLogger = LogFactory.getLog(this.getClass());
    
    private ProxyService fProxyService;

    /**
     */
    public IPlugProxyFactoryImpl() {
        // for serialization
    }

    /**
     * @param communication
     */
    public IPlugProxyFactoryImpl(ICommunication communication) {
        this.fProxyService=new ProxyService();
        this.fProxyService.setCommunication(communication);
    }

    /**
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription)
     */
    public IPlug createPlugProxy(PlugDescription plug) throws Exception {
        IPlug result = null;

        final String wetagUrl = plug.getProxyServiceURL();
        final Class iPlugClass = Thread.currentThread().getContextClassLoader().loadClass(plug.getIPlugClass());

        RemoteInvocationController ric = null;
        try {
            ric = this.fProxyService.createRemoteInvocationController(wetagUrl);
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
