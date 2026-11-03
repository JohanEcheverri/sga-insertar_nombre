package co.uniquindio.edu.sga.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos de documentación OpenAPI para la API REST del SGA. */
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGA API - Sistema de Gestión de Alojamientos")
                        .version("1.0")
                        .description("Endpoints REST para gestionar alojamientos, apartamentos, " +
                                "reservas, temporadas, usuarios, folios y reportes."));
    }
}
