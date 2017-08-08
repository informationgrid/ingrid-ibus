package de.ingrid.ibus;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.ingrid.admin.Config;
import de.ingrid.admin.JettyStarter;
import de.ingrid.admin.elasticsearch.converter.QueryConverter;
import de.ingrid.admin.service.ElasticsearchNodeFactoryBean;

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (developmentMode) {
            initDevelopmentMode( http );
        } else {
            initProductionMode( http );
        }
    }

    @Bean
    public QueryConverter queryConverter() throws Exception {
        return new QueryConverter();
    }
    
    @Bean
    public ElasticsearchNodeFactoryBean elasticsearchNodeFactoryBean() throws Exception {
        Config config = new Config();
        config.indexing = true;
        config.esRemoteNode = true;
        config.indexSearchInTypes = new ArrayList<String>();
        config.communicationProxyUrl = this.indexName;
        config.additionalSearchDetailFields = new String[0];
        config.indexSearchDefaultFields = this.defaultFields;
        config.indexFieldTitle = this.titleField;
        config.indexFieldSummary = this.summaryField;
        config.esRemoteHosts = this.remoteHosts;
        
        new JettyStarter( false );
        JettyStarter.getInstance().config = config;
        
        return new ElasticsearchNodeFactoryBean();
    }

    private void initProductionMode(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // make cookies readable within JS
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
            // .disable();
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }

    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //// auth
    //// .inMemoryAuthentication()
    //// .withUser("user").password("password").roles("USER");
    //
    //// auth.userDetailsService( username -> {
    //// return new User( username, password, authorities );
    //// });
    //
    //
    // }
}
