/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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

package de.ingrid.ibus.comm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.comm.net.PlugQueryRequest;
import de.ingrid.utils.IngridHits;

/**
 * 
 */
public class ResultSet extends ArrayList {

    private static final long serialVersionUID = ResultSet.class.getName().hashCode();
    
    private final static Log LOG = LogFactory.getLog(ResultSet.class);

    private int fNumberOfConnections;

    private int fNumberOfFinsihedConnections;

	private final boolean fAllowEmptyResults;

    /**
	 * Stores the result of different processes. Has functionality to show if
	 * all processes added their result.
	 * 
	 * @param allowEmptyResults
	 * 
	 * @param numberOfConnections
	 *            The number of results that ResultSet should be contain.
	 */
    public ResultSet(boolean allowEmptyResults, int numberOfConnections) {
		this.fAllowEmptyResults = allowEmptyResults;
		this.fNumberOfConnections = numberOfConnections;
    }

    /**
     * Checks if all results are added.
     * 
     * @return True if all results are finished and false if not.
     */
    public synchronized boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

    /**
     * Should be called if a result is added. If all results are added it notifies waiting threads.
     */
    public synchronized void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
        if (isComplete()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug( "Resultset is complete. Notify [" + this + "] in thread [" + Thread.currentThread().getName() + "]." );
            }
            this.notify();
        }
    }

    /**
     * This method is for adding a IngridHits object from a search run.
     * 
     * @param hits
     *            The hits to be added. This cannot be null.
     * @return True if a result is added otherwise false.
     */
    public synchronized boolean add(IngridHits hits) {
        if (hits == null) {
            throw new IllegalArgumentException("Null can not be added as hits.");
        }
		boolean ret = false;
		boolean empty = hits.getHits() == null || hits.getHits().length == 0;
		if (empty && !fAllowEmptyResults) {
			ret = false;
		} else {
			ret = super.add(hits);
        }
		return ret;
    }

    /**
     * Is for getting a list of all plugs that should add hits to the ResultSet.
     * 
     * @return All plug ids from the plugs which delivers a result.
     */
    public String[] getPlugIdsWithResult() {
        List plugIds = new ArrayList(size());
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            IngridHits hits = (IngridHits) iter.next();
            plugIds.add(hits.getPlugId());
        }
        return (String[]) plugIds.toArray(new String[plugIds.size()]);
    }
}
