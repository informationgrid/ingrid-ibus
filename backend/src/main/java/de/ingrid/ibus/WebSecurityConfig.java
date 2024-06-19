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
package de.ingrid.ibus;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.comm.ICodeListCommunication;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.XmlCodeListPersistency;
import de.ingrid.ibus.config.CodelistConfiguration;
import de.ingrid.ibus.config.ElasticsearchConfiguration;
import de.ingrid.ibus.config.IBusConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({CodelistConfiguration.class, ElasticsearchConfiguration.class, IBusConfiguration.class})
@EnableWebSecurity
public class WebSecurityConfig {

    private static final Logger log = LogManager.getLogger(WebSecurityConfig.class);

    @Value("${development:false}")
    private boolean developmentMode;

    @Value("${app.enable.cors:false}")
    private boolean enableCors;

    @Value("${app.enable.csrf:true}")
    private boolean enableCsrf;

    @Value("${codelistrepo.url:http://not-configured}")
    private String codelistUrl;

    @Value("${codelistrepo.username:}")
    private String codelistUsername;

    @Value("${codelistrepo.password:}")
    private String codelistPassword;

    private final UserDetailsService userDetailsService;

    public boolean isPasswordDefined = false;

    @Value("${spring.security.user.password:}")
    private String password;

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!password.isEmpty()) {
            this.isPasswordDefined = true;
        }

        if (developmentMode) {
            return initDevelopmentMode(http);
        } else {
            return initProductionMode(http);
        }
    }

    // ************************
    // Use encoded passwords for authentication
    // ************************

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }*/

    // ************************


    // ************************
    // Codelist Service
    // ************************
    @Bean
    CodeListService codelistService() {
        CodeListService codeListService = new CodeListService();
        codeListService.setComm(codelistCommunication());

        List<ICodeListPersistency> persistencies = new ArrayList<>();
        XmlCodeListPersistency<?> xmlPersistency = new XmlCodeListPersistency<>();
        xmlPersistency.setPathToXml("data/codelists");
        persistencies.add(xmlPersistency);
        codeListService.setPersistencies(persistencies);

        codeListService.setDefaultPersistency(0);
        return codeListService;
    }

    public void secureWebapp(String adminPassword) {
        this.isPasswordDefined = true;
        InMemoryUserDetailsManager userService = (InMemoryUserDetailsManager) this.userDetailsService;
        UserDetails adminUser = new User("admin", adminPassword, new ArrayList<>());
        userService.updateUser(adminUser);
    }

    private ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl(codelistUrl + "/rest/getCodelists");
        comm.setUsername(codelistUsername);
        comm.setPassword(codelistPassword);
        return comm;
    }
    // ************************

    private SecurityFilterChain initProductionMode(HttpSecurity http) throws Exception {

        if (enableCsrf) {
            http = http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())); // make cookies readable within JS
        } else {
            http = http.csrf(AbstractHttpConfigurer::disable);
        }

        if (!enableCors) {
            http = http.cors(AbstractHttpConfigurer::disable);
        }

        return http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/login*").permitAll()
                        .anyRequest().access(customAuthManager()))
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    private SecurityFilterChain initDevelopmentMode(HttpSecurity http) throws Exception {
        log.info("======================================================");
        log.info("================== DEVELOPMENT MODE ==================");
        log.info("======================================================");
        return http
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    AuthorizationManager<RequestAuthorizationContext> customAuthManager() {
        return (authentication, object) -> {
            // make authorization decision
            if (!isPasswordDefined || !authentication.get().getPrincipal().equals("anonymousUser")) {
                return new AuthorizationDecision(true);
            }
            return new AuthorizationDecision(false);
        };
    }

}
