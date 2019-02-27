/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({CodelistConfiguration.class, ElasticsearchConfiguration.class, IBusConfiguration.class})
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger log = LogManager.getLogger( WebSecurityConfig.class );

    @Value("${development:false}")
    private boolean developmentMode;

    @Value("${codelistrepo.url:http://not-configured}")
    private String codelistUrl;
    
    @Value("${codelistrepo.username:}")
    private String codelistUsername;
    
    @Value("${codelistrepo.password:}")
    private String codelistPassword;

    private final SecurityService securityService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(SecurityService securityService, UserDetailsService userDetailsService) {
        this.securityService = securityService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (developmentMode) {
            initDevelopmentMode( http );
        } else {
            initProductionMode( http );
        }
    }

    // ************************
    // Use encoded passwords for authentication
    // ************************

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    // ************************


    // ************************
    // Codelist Service
    // ************************
    @Bean
    public CodeListService codelistService() {
        CodeListService codeListService = new CodeListService();
        codeListService.setComm(codelistCommunication());

        List<ICodeListPersistency> persistencies = new ArrayList<>();
        XmlCodeListPersistency<?> xmlPersistency = new XmlCodeListPersistency<>();
        xmlPersistency.setPathToXml( "data/codelists" );
        persistencies.add( xmlPersistency );
        codeListService.setPersistencies(persistencies);

        codeListService.setDefaultPersistency( 0 );
        return codeListService;
    }

    public void secureWebapp(String adminPassword) {
        this.securityService.isPasswordDefined = true;
        InMemoryUserDetailsManager userService = (InMemoryUserDetailsManager) this.userDetailsService;
        UserDetails adminUser = new User("admin", adminPassword, new ArrayList<>());
        userService.updateUser(adminUser);
    }

    private ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl( codelistUrl + "/rest/getCodelists" );
        comm.setUsername( codelistUsername );
        comm.setPassword( codelistPassword );
        return comm;
    }
    // ************************

    private void initProductionMode(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // make cookies readable within JS
                .and()
            .authorizeRequests()
                .antMatchers( "/css/**" ).permitAll()
                .and()
            .authorizeRequests()
                .anyRequest()
                .access("@security.hasPermission(authentication)")
                .and()
            .formLogin()
                .loginPage( "/login" )
                .permitAll()
                .and()
            .logout()
                .permitAll();
        // @formatter:on
    }

    private void initDevelopmentMode(HttpSecurity http) throws Exception {
        log.info( "======================================================" );
        log.info( "================== DEVELOPMENT MODE ==================" );
        log.info( "======================================================" );
        // @formatter:off
        http
            .cors().and()
            .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
            .csrf()
                .disable();
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }

}
