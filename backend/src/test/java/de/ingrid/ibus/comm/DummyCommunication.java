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
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.comm;

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
