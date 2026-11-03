package co.uniquindio.edu.sga.infraestructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Punto de configuración para servicios del dominio que requieran ensamblaje manual.
 * Los servicios de dominio actuales que tienen {@code @Component} ya los registra el
 * escaneo de componentes. RetencionService depende de la configuración propia de un
 * alojamiento, así que no corresponde crearla como singleton global.
 */
@Configuration(proxyBeanMethods = false)
public class DomainConfig {
}
