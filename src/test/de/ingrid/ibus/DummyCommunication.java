/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  $Source:  $
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

    public void subscribeGroup(String url) throws Exception, IllegalArgumentException {
        //s
    }

    public void unsubscribeGroup(String url) throws Exception {
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

}
