/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus;

import java.io.IOException;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.messaging.IMessageQueue;
import net.weta.components.communication.messaging.Message;

/**
 * DummyCommunication
 * 
 * <p/>created on 11.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class DummyCommunication implements ICommunication {

    public IMessageQueue getMessageQueue() {
        return null;
    }

    public void subscribeGroup(String url) throws IOException {
        //s
    }

    public void unsubscribeGroup(String url) throws IOException {
        // TODO Auto-generated method stub

    }

    public boolean isSubscribed(String url) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return false;
    }

    public void sendMessage(Message message, String url) throws IOException, IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public Message sendSyncMessage(Message message, String url) throws IOException, IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPeerName() {
        // TODO Auto-generated method stub
        return null;
    }

    public void startup() throws IOException {
        // TODO Auto-generated method stub

    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

    public void closeConnection(String url) throws IOException {
        // TODO Auto-generated method stub
        
    }

    public void setPeerName(String peerName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isConnected(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
