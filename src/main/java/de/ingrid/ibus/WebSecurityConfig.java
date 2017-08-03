package de.ingrid.ibus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.ingrid.admin.elasticsearch.converter.QueryConverter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger log = LogManager.getLogger( WebSecurityConfig.class );

    @Value("${development:false}")
    private boolean developmentMode;

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
