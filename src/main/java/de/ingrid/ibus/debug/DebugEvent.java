package de.ingrid.ibus.debug;

import java.util.List;

public class DebugEvent {
    public Long duration = null;
    public String title;
    public String message = "";
    public List<String> messageList = null;

    public DebugEvent( String title, String msg ) {
        this.title   = title;
        this.message = msg;
    }
    
    public DebugEvent( String title, List<String> msgList ) {
        this.title   = title;
        this.messageList = msgList;
    }
}