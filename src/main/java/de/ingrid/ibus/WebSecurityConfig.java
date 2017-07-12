package de.ingrid.ibus;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static Logger log = LogManager.getLogger(WebSecurityConfig.class);

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

    private void initProductionMode(HttpSecurity http) throws Exception {
        // @formatter:off
        http
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
        log.info("======================================================");
        log.info("================== DEVELOPMENT MODE ==================");
        log.info("======================================================");
        // @formatter:off
        http
            .cors().and()
            .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
            .csrf().requireCsrfProtectionMatcher( new RequestMatcher() {
        // @formatter:on

                    private RegexRequestMatcher apiMatcher = new RegexRequestMatcher( "/api/.*", null );

                    @Override
                    public boolean matches(HttpServletRequest req) {
                        // disable CSRF for all API requests
                        if (apiMatcher.matches( req )) {
                            return false;
                        }
                        return true;
                    }
                } );
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
