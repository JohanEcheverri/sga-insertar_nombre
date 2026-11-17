package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/** Crea y valida tokens JWT usados para autenticar solicitudes. */
@Service
public class JwtService {
    private final SecretKey llave;
    private final String emisor;
    private final long expiracionMinutos;

    public JwtService(SecretKey llave,
                      @Value("${sga.jwt.emisor:sga-uniquindio}") String emisor,
                      @Value("${sga.jwt.expiracion-minutos:60}") long expiracionMinutos) {
        if (expiracionMinutos <= 0) throw new IllegalArgumentException("La expiración JWT debe ser positiva.");
        this.llave = llave;
        this.emisor = emisor;
        this.expiracionMinutos = expiracionMinutos;
    }

    public TokenCreado generar(UUID usuarioId, String correo, RolUsuario rol) {
        Instant ahora = Instant.now();
        Instant vence = ahora.plusSeconds(expiracionMinutos * 60);
        List<String> roles = List.of(rol.name());
        String token = Jwts.builder().issuer(emisor).subject(correo)
                .claim("usuarioId", usuarioId.toString())
                .claim("correo", correo)
                .claim("roles", roles)
                .issuedAt(Date.from(ahora)).expiration(Date.from(vence))
                .signWith(llave).compact();
        return new TokenCreado(token, vence, roles);
    }

    public Identidad validarYExtraer(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(llave).requireIssuer(emisor).build()
                    .parseSignedClaims(token).getPayload();
            UUID usuarioId = UUID.fromString(claims.get("usuarioId", String.class));
            String correo = claims.get("correo", String.class);
            List<?> rolesClaim = claims.get("roles", List.class);
            if (correo == null || rolesClaim == null || rolesClaim.isEmpty()) throw new JwtException("Claims incompletos.");
            RolUsuario rol = RolUsuario.valueOf(rolesClaim.getFirst().toString());
            return new Identidad(usuarioId, correo, rol);
        } catch (JwtException | IllegalArgumentException | NullPointerException ex) {
            throw new BadCredentialsException("El token de acceso no es válido.", ex);
        }
    }

    public record TokenCreado(String token, Instant expireAt, List<String> roles) { }
    public record Identidad(UUID usuarioId, String correo, RolUsuario rol) { }
}
