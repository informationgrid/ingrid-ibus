/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
import de.ingrid.ibus.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({CodelistConfiguration.class, ElasticsearchConfiguration.class, IBusConfiguration.class})
public class WebSecurityConfig {

    private static Logger log = LogManager.getLogger(WebSecurityConfig.class);

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

    private final SecurityService securityService;
    private final InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();


    public WebSecurityConfig(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityService securityService) throws Exception {
        if (developmentMode) {
            return http
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable())
                    .build();
        } else {
            HttpSecurity httpSecurity = http;

            if (enableCsrf) {
                httpSecurity = httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())); // make cookies readable within JS
            } else {
                httpSecurity = httpSecurity.csrf(csrf -> csrf.disable());
            }

            if (enableCors) {
                httpSecurity = httpSecurity.cors(Customizer.withDefaults());
            } else {
                httpSecurity = httpSecurity.cors(cors -> cors.disable());
            }

            return httpSecurity
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/css/**").permitAll()
                            .requestMatchers("/login*").permitAll()
                            .anyRequest().access((authentication, context) ->
                                    new AuthorizationDecision(securityService.hasPermission(authentication.get())))
                    )
                    .formLogin(formLogin -> formLogin
                            .loginPage("/login")
                            .permitAll()
                    )
                    .logout(logout -> logout.permitAll())
                    .build();
        }
    }

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
        this.securityService.isPasswordDefined = true;
        UserDetails adminUser = User.withUsername("admin")
                .password(passwordEncoder().encode(adminPassword))
                .roles()
                .build();
        this.userDetailsService.updateUser(adminUser);
    }

    private ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl(codelistUrl + "/rest/getCodelists");
        comm.setUsername(codelistUsername);
        comm.setPassword(codelistPassword);
        return comm;
    }
}
