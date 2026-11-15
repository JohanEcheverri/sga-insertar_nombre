package co.uniquindio.edu.sga.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;

/** Configuración CORS desde la variable SGA_CORS_ALLOWED_ORIGINS. */
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {
    @Value("${sga.cors.allowed-origins:http://localhost:5173}")
    private String origenesPermitidos;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origenes = Arrays.stream(origenesPermitidos.split(","))
                .map(String::trim).filter(origen -> !origen.isEmpty()).toArray(String[]::new);
        registry.addMapping("/**")
                .allowedOrigins(origenes)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
