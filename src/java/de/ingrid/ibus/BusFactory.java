/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus;

import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;

/**
 * Factory for easy remote IBus-object creation.
 */
public class BusFactory {

    private RemoteInvocationController fRIController = null;

    /**
     * @param ric
     * @throws Exception
     */
    public BusFactory(ProxyService proxyService, String url) throws Exception {
        this.fRIController = proxyService.createRemoteInvocationController(url);
    }

    /**
     * @return The IBus implementation.
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws Exception
     * @throws Throwable
     */
    public Bus getRemoteIBus() throws SecurityException, NoSuchMethodException, Exception, Throwable {
        Bus result = null;

        result = (Bus) this.fRIController.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);

        return result;
    }
}
