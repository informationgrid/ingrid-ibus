package de.ingrid.ibus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class IBusConfiguration {

    public final IBus ibus = new IBus();
    public final Codelistrepo codelistrepo = new Codelistrepo();
    public final Elasticsearch elastic = new Elasticsearch();
    public final Server server = new Server();
    public Spring spring = new Spring();

    public IBus getIbus() {
        return ibus;
    }

    public Elasticsearch getElastic() {
        return elastic;
    }

    public Server getServer() {
        return server;
    }

    public Spring getSpring() {
        return spring;
    }


    public Codelistrepo getCodelistrepo() {
        return this.codelistrepo;
    }


    public static class Server {
        public String port;

        public void setPort(String port) {
            this.port = port;
        }
    }

    public static class IBus {
        public String url;

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Codelistrepo {
        public String username;
        public String password;
        public String url;

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Elasticsearch {
        public String[] remoteHosts;

        public void setRemoteHosts(String[] remoteHosts) {
            this.remoteHosts = remoteHosts;
        }
    }

    public static class Spring {
        public final Security security = new Security();
        public Security getSecurity() {
            return security;
        }

        public static class Security {

            public final User user = new User();
            public User getUser() {
                return user;
            }

            public static class User {
                public String password;

                public void setPassword(String password) {
                    this.password = password;
                }
            }
        }
    }

}
