package co.uniquindio.edu.sga.infraestructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(UsuarioUserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${sga.cors.allowed-origins:http://localhost:5173,http://localhost:5174}") String origenes) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origenes.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setAllowCredentials(false);
        cors.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService,
            AutenticacionEntryPoint entryPoint, AccesoDenegadoHandler deniedHandler,
            AuthenticationManager authenticationManager,
            @Value("${sga.seguridad.herramientas-desarrollo:false}") boolean herramientasDesarrollo) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> { })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/login", "/api/auth/registro", "/error", "/actuator/health").permitAll();
                    auth.requestMatchers("/api/apartamentos/preparacion-pendiente",
                            "/api/apartamentos/*/novedades/**", "/api/novedades/**",
                            "/api/reservas/*/registrar-llegada", "/api/reservas/*/registrar-salida",
                            "/api/reservas/*/folio/**", "/api/reservas/llegadas", "/api/reservas/salidas")
                            .hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.GET, "/api/apartamentos/**", "/api/cotizaciones/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/cotizaciones").permitAll();
                    if (herramientasDesarrollo) {
                        auth.requestMatchers("/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll();
                    }
                    auth.requestMatchers("/api/apartamentos/**", "/api/temporadas/**", "/api/tarifas/**",
                            "/api/politicas-cancelacion/**", "/api/reportes/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.GET, "/api/reservas").hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR");
                    auth.requestMatchers("/api/reservas/**", "/api/usuarios/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, entryPoint), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
