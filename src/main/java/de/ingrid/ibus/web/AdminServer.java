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

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.UserRealm;

/**
 * 
 */
public class AdminServer {

  protected final static Log fLogger = LogFactory.getLog(AdminServer.class);
  
  private static final String PLUG_DESCRIPTION = "conf/plugdescription.xml";
  
    /**
     * To start the admin web server from the commandline. 
     * @param args The server port and the web app folder. 
     * @throws Exception Something goes wrong.
     */
    public static void main(String[] args) throws Exception {
        String usage = "<serverPort> <webappFolder>";
        if ((args.length != 2) && (args.length != 4)) {
            System.err.println(usage);
            return;
        }
        
        Map arguments = readParameters(args);
        File plugDescriptionFile = new File(PLUG_DESCRIPTION);
        if (arguments.containsKey("--plugdescription")) {
          plugDescriptionFile = new File((String) arguments.get("--plugdescription"));
       }
        
        int port = Integer.parseInt(args[0]);
        File webFolder = new File(args[1]);
        Map map = new HashMap();
        map.put("pd_file", plugDescriptionFile);
        WebContainer container = startWebContainer(map, port, webFolder, false, null);
        container.join();
    }

    /**
     * Starts a web container with jetty.
     * @param plugdescriptionFilename 
     * @param port The port for the web server.
     * @param webFolder The folder where the web contexts are located.
     * @param secure True if authentication is requiered otherwise false.
     * @param realm A user password relation if it is a secure web container. 
     * @param busClient The bus client for the communication.
     * @return The started WebContainer.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws Exception
     * @throws InterruptedException
     */
    public static WebContainer startWebContainer(Map attributes, int port, File webFolder, boolean secure, UserRealm realm)
            throws IOException, NoSuchAlgorithmException, Exception, InterruptedException {
        WebContainer container = new WebContainer(port, secure);
        container.setAttribues(attributes);
        if (secure) {
            container.setRealm(realm);
        }
        container.startContainer();
        File[] files = webFolder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String file = files[i].getCanonicalPath();
                container.addWebapp(files[i].getName(), file);
            }
        }

        return container;
    }
    
    private static Map readParameters(String[] args) {
      Map argumentMap = new HashMap();
      // convert and validate the supplied arguments
      if (2 != args.length && 4 != args.length) {
          printUsage();
          System.exit(1);
      }
      for (int i = 0; i < args.length; i = i + 2) {
          argumentMap.put(args[i], args[i + 1]);
      }
      return argumentMap;
  }
    
    private static void printUsage() {
      System.err
              .println("Usage: You must set --descriptor <filename> for jxta or <multicastport> <unicastport> <IBusHost> <IBusPort> for socket communication");
  }
}
