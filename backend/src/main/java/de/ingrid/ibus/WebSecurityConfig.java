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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

    @Value("${spring.security.user.password:}")
    private String ibusPassword;

    private final SecurityService securityService;
    private final InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public WebSecurityConfig(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        if (this.securityService.isPasswordDefined) {
            userDetailsService.createUser(
                    User.withUsername("admin")
                            .password(ibusPassword)
                            .roles("admin")
                            .build());
        }
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityService securityService) throws Exception {
        if (developmentMode) {
            return http
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable)
                    .build();
        } else {

            if (enableCsrf) {
                http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));
            } else {
                http.csrf(AbstractHttpConfigurer::disable);
            }

            if (enableCors) {
                http.cors(Customizer.withDefaults());
            } else {
                http.cors(AbstractHttpConfigurer::disable);
            }

            return http
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
                    .logout(LogoutConfigurer::permitAll)
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
                .password(adminPassword) // password already is encrypted
                .roles()
                .build();
        if (this.userDetailsService.userExists("admin"))
            this.userDetailsService.updateUser(adminUser);
        else
            this.userDetailsService.createUser(adminUser);
    }

    private ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl(codelistUrl + "/rest/getCodelists");
        comm.setUsername(codelistUsername);
        comm.setPassword(codelistPassword);
        return comm;
    }

    // See here: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
    static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
            /*
             * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
             * the CsrfToken when it is rendered in the response body.
             */
            this.xor.handle(request, response, csrfToken);
            /*
             * Render the token value to a cookie by causing the deferred token to be loaded.
             */
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            /*
             * If the request contains a request header, use CsrfTokenRequestAttributeHandler
             * to resolve the CsrfToken. This applies when a single-page application includes
             * the header value automatically, which was obtained via a cookie containing the
             * raw CsrfToken.
             *
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
