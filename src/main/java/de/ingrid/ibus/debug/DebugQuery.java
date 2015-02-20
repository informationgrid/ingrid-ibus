package de.ingrid.ibus.debug;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.utils.query.IngridQuery;

public class DebugQuery {
    private IngridQuery query;
    
    private List<DebugEvent> events = new ArrayList<DebugEvent>();

    private boolean isActive = false;
    
    
    public DebugQuery() {}
    
    public void addEvent( DebugEvent e ) {
        events.add( e );
    }
    
    public List<DebugEvent> getEvents() {
        return events;
    }

    public boolean isActive() {
        return this.isActive;
    }
    
    public void setActiveAndReset() {
        this.isActive = true;
        this.setQuery( null );
        this.events.clear();
    }
    
    public void setInactive() {
        this.isActive = false;        
    }

    public IngridQuery getQuery() {
        return query;
    }

    public void setQuery(IngridQuery query) {
        this.query = query;
    }
    
}
