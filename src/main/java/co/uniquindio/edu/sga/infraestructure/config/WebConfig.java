package co.uniquindio.edu.sga.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuración web compartida por los endpoints REST. */
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

    /**
     * Permite solicitudes desde cualquier origen durante el desarrollo local.
     * No habilita credenciales, por lo que el comodín de origen es válido.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .maxAge(3600);
    }

}
