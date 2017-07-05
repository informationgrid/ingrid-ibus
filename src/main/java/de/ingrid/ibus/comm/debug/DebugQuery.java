/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.ibus.comm.debug;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.utils.query.IngridQuery;

public class DebugQuery {
    private IngridQuery query;
    
    private List<DebugEvent> events = new ArrayList<DebugEvent>();

    private boolean isActive = false;

    private boolean analysisInProgress = false;
    
    
    public DebugQuery() {}
    
    public void addEvent( DebugEvent e ) {
        events.add( e );
    }
    
    public List<DebugEvent> getEvents() {
        return events;
    }

    /**
     * This must be the entry method to start an analysis. 
     * Check if debugging is active AND that there's not an analysis in progress.
     * 
     * @return true if debugging is allowed and no analysis is in progress yet
     */
    public boolean canDebugNow() {
        boolean allow = this.isActive && !this.analysisInProgress;
        if (!allow) {
            this.analysisInProgress = true;
        }
        return allow;
    }
    
    public boolean isActive() {
        return this.isActive;
    }

    public boolean isActive(IngridQuery other) {
        return this.isActive && (this.query.hashCode() == other.hashCode());
    }
    
    public void setActiveAndReset() {
        this.isActive = true;
        this.setQuery( null );
        this.events.clear();
        this.analysisInProgress = false;
    }
    
    public void setInactive() {
        this.isActive = false;  
        this.analysisInProgress = false;
    }

    public IngridQuery getQuery() {
        return query;
    }

    public void setQuery(IngridQuery query) {
        this.query = query;
    }

}
