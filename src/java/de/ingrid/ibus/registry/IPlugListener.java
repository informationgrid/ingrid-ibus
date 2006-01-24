/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.registry;

/**
 */
public interface IPlugListener {

    /**
     * Is called when a IPlug is removed from the registry.
     * 
     * @param iPlugId
     */
    public void removeIPlug(String iPlugId);
}
