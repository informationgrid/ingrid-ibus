package de.ingrid.ibus;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.comm.ICodeListCommunication;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.XmlCodeListPersistency;
import de.ingrid.ibus.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger log = LogManager.getLogger( WebSecurityConfig.class );

    @Value("${development:false}")
    private boolean developmentMode;

    @Value("${elastic.remoteHosts:localhost:9300}")
    private String[] remoteHosts;

    @Value("${elastic.defaultFields:title,content}")
    private String[] defaultFields;

    @Value("${elastic.indexName:__centralIndex__}")
    private String indexName;

    @Value("${elastic.indexFieldTitle:title}")
    private String titleField;

    @Value("${elastic.indexFieldSummary:abstract}")
    private String summaryField;
    
    @Value("${codelistrepo.url:http://not-configured}")
    private String codelistUrl;
    
    @Value("${codelistrepo.username:}")
    private String codelistUsername;
    
    @Value("${codelistrepo.password:}")
    private String codelistPassword;

    @Value("${spring.security.user.password:}")
    private String userPassword;

    @Autowired
    private SecurityService securityService;

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
    @Autowired
    UserDetailsService userDetailsService;

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

    public void secureWebapp(String adminPassword) throws Exception {
        this.securityService.isPasswordDefined = true;
        InMemoryUserDetailsManager userService = (InMemoryUserDetailsManager) this.userDetailsService;
        UserDetails adminUser = new User("admin", adminPassword, new ArrayList<>());
        userService.updateUser(adminUser);
    }

    private ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl( codelistUrl );
        comm.setUsername( codelistUsername );
        comm.setPassword( codelistPassword );
        return comm;
    }
    // ************************

    private void initProductionMode(HttpSecurity http) throws Exception {
        // @formatter:off
        LogoutConfigurer<HttpSecurity> configurer = http
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
