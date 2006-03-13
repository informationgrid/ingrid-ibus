/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 */
public class ResultSet extends ArrayList {

	private static final long serialVersionUID = ResultSet.class.getName()
			.hashCode();

	private int  fNumberOfConnections;

	private int fNumberOfFinsihedConnections = 0;

	private Object fMonitor;

	/**
	 * @param numberOfConnections
	 * @param monitor
	 */
	public ResultSet(int numberOfConnections, Object monitor) {
		this.fNumberOfConnections = numberOfConnections;
		this.fMonitor = monitor;
	}

	/**
	 * @return if all connections are finished
	 */
	public synchronized boolean isComplete() {
		return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
	}

	/**
     * 
     */
    public synchronized void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
        if (isComplete()) {
            synchronized (fMonitor) {
                fMonitor.notify();
            }
        }
    }



	public synchronized boolean add(Object arg0) {
		if (arg0 == null) {
			throw new IllegalArgumentException("null can not added as Hits");
		}
		return super.add(arg0);
	}
}
