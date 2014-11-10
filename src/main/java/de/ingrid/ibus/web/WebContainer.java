/*
 * **************************************************-
 * Ingrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.ibus.web;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SocketListener;
import org.mortbay.http.UserRealm;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.HashSessionManager;
import org.mortbay.jetty.servlet.WebApplicationContext;


/**
 * A container for web application contexts.
 */
public class WebContainer extends Thread {

    private static Log log = LogFactory.getLog(WebContainer.class);

    private static final int SESSION_TIMEOUT = 60 * 60 * 8; // 8 days for timeout...

    private static Server fServer;

    private int fPort;

    private UserRealm fRealm;

    private boolean fSecured;
    
    private Map _attributes = new HashMap();

    /**
     * Initializes the WebContainer.
     * @param port The port to the web server.
     * @param secured True if it should be secured by authorization otherwise false.
     */
    public WebContainer(int port, boolean secured) {
        this.fPort = port;
        this.fSecured = secured;
    }

    public void run() {
        try {
            fServer = new Server();
            // Create a port listener
            SocketListener listener = new SocketListener();
            listener.setPort(this.fPort);
            fServer.addListener(listener);

            if (this.fSecured) {
                fServer.addRealm(this.fRealm);
            }

            fServer.start();
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Adds a web application to the container.
     * @param name A name for the application.
     * @param path The path of the application.
     * @param pd_filename The name of the plug description file.
     * @throws Exception
     */
    public void addWebapp(String name, String path) throws Exception {
        if (fServer == null || !fServer.isStarted()) {
            throw new IOException("web container not started");
        }
        WebApplicationContext context = fServer.addWebApplication('/' + name, path);
        ((HashSessionManager) context.getServletHandler().getSessionManager()).setCrossContextSessionIDs(true);
        context.getServletHandler().setSessionInactiveInterval(SESSION_TIMEOUT);

        if (this.fSecured) {
            SecurityHandler handler = new SecurityHandler();
            handler.setAuthMethod("BASIC");
            context.addHandler(handler);
            context.setAuthenticator(new BasicAuthenticator());
            SecurityConstraint sc = new SecurityConstraint();
            sc.setAuthenticate(true);
            sc.addRole(SecurityConstraint.ANY_ROLE);
            context.addSecurityConstraint("/", sc);
        }
        context.setAttribute("server", this);
        Set set = _attributes.keySet();
        for (Iterator iter = set.iterator(); iter.hasNext();) {
          String key = (String) iter.next();
          Object value = _attributes.get(key);
          context.setAttribute(key, value);
        }
        context.start();
    }
    
    /**
     * Starts the web container.
     * @throws IOException
     */
    public void startContainer() throws IOException {
        int i = 3;
        start();

        while ((null == fServer) || (!fServer.isStarted())) {
            i--;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                log.error(ie.getMessage(), ie);
            }
            
            if (0 == i) {
                throw new IOException("Could not start web container");
            }
        }
    }

    /**
     * Stops the web container.
     * 
     * @throws InterruptedException
     */
    public void stopContainer() throws InterruptedException {
        if (fServer != null && fServer.isStarted()) {
            fServer.stop();
        }
    }

    /**
     * Logout a user.
     * 
     * @param principal The user.
     */
    public void logoutUser(Principal principal) {
        if (this.fRealm != null) {
            this.fRealm.disassociate(principal);
            this.fRealm.logout(principal);
        } else {
            log.warn("No realm is set.");
        }
    }

    /**
     * Removes a user.
     * 
     * @param userName The name of the user.
     */
    public void removeUser(String userName) {
        fServer.removeRealm(userName);
    }

    /**
     * Returns the currently used realm.
     * @return The used realm.
     */
    public UserRealm getRealm() {
        return this.fRealm;
    }

    /**
     * Sets the realm for authentication.
     * @param realm The relam to use.
     */
    public void setRealm(UserRealm realm) {
        this.fRealm = realm;
    }

        
    public void addAttribute(String key, Object value) {
        _attributes.put(key, value);
    }

    public void setAttribues(Map attributes) {
        _attributes = attributes;
    }
}
