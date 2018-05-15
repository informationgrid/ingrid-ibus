package de.ingrid.ibus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    
    @Value("${development:false}")
    private boolean developmentMode;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController( "/login" ).setViewName( "login" );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (this.developmentMode) {
            registry.addMapping( "/api/**" ).allowedOrigins( "http://localhost:4200" );
        }
    }

}