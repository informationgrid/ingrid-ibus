/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.ibus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("elastic")
public class ElasticsearchConfiguration {

    private String[] remoteHosts;
    private String username;
    private String password;
    private String sslTransport;


    public String[] getRemoteHosts() {
        return remoteHosts;
    }

    public void setRemoteHosts(String[] remoteHosts) {
        this.remoteHosts = remoteHosts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSslTransport() {
        return sslTransport;
    }

    public void setSslTransport(String sslTransport) {
        this.sslTransport = sslTransport;
    }
}
