package co.uniquindio.edu.sga.infraestructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
public class JwtConfig {
    @Bean
    public SecretKey jwtSecretKey(@Value("${SGA_JWT_SECRET:${sga.jwt.secret}}") String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("El secreto JWT debe contener al menos 32 bytes para HS256.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
