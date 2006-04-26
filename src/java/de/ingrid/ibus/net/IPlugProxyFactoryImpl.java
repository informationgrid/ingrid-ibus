/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus.net;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ProxyService;
import de.ingrid.utils.IPlug;
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
        this.fCommunication=communication;
    }

    /**
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription) throws Exception {
        final String plugUrl = plugDescription.getProxyServiceURL();
        IPlug plug = (IPlug) ProxyService.createProxy(this.fCommunication,IPlug.class, plugUrl);
//        plug.configure(plugDescription);
//        try {
//            ric = this.fProxyService.createRemoteInvocationController(plugUrl);
//            result = (IPlug) ric.newInstance(iPlugClass, null, null);
//            result.configure(plug);
//        } catch (ConnectException e) {
//            throw e;
//        } catch (Throwable t) {
//            this.fLogger.error(t.getMessage(), t);
//        }

        return plug;
    }
}
