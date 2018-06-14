package de.ingrid.ibus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "de.ingrid" })
public class IBusApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run( IBusApplication.class, args );
    }

}
