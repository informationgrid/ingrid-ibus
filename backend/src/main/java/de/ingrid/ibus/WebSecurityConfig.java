package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.comm.ICodeListCommunication;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.XmlCodeListPersistency;

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
    
    @Value("${codelistrepo.url:http://192.168.0.247/xxx}")
    private String codelistUrl;
    
    @Value("${codelistrepo.username:}")
    private String codelistUsername;
    
    @Value("${codelistrepo.password:}")
    private String codelistPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (developmentMode) {
            initDevelopmentMode( http );
        } else {
            initProductionMode( http );
        }
    }

    @Bean
    public CodeListService codelistService() {
        CodeListService codeListService = new CodeListService();
        List<ICodeListPersistency> persistencies = new ArrayList<>();
        XmlCodeListPersistency<?> xmlPersistency = new XmlCodeListPersistency<>();
        xmlPersistency.setPathToXml( "data/codelists" );
        persistencies.add( xmlPersistency );
        codeListService.setPersistencies( persistencies  );
        codeListService.setDefaultPersistency( 0 );
        return codeListService;
    }
    
    @Bean
    public ICodeListCommunication codelistCommunication() {
        HttpCLCommunication comm = new HttpCLCommunication();
        comm.setRequestUrl( codelistUrl );
        comm.setUsername( codelistUsername );
        comm.setPassword( codelistPassword );
        return comm;
    }

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
                .anyRequest().authenticated()
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
