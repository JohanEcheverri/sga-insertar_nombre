package co.uniquindio.edu.sga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/** Punto de entrada de Spring Boot y configuración del reloj de la aplicación. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SgaApplication {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    public static void main(String[] args) {
        SpringApplication.run(SgaApplication.class, args);
    }

}
