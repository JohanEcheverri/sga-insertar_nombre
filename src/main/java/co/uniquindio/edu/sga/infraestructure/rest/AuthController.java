package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.LoginRequest;
import co.uniquindio.edu.sga.application.dto.request.RegistroRequest;
import co.uniquindio.edu.sga.application.dto.response.TokenResponse;
import co.uniquindio.edu.sga.application.usecase.CrearUsuarioUseCase;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import co.uniquindio.edu.sga.infraestructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CrearUsuarioUseCase crearUsuario;
    private final UsuarioRepository usuarios;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          CrearUsuarioUseCase crearUsuario, UsuarioRepository usuarios) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.crearUsuario = crearUsuario;
        this.usuarios = usuarios;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest solicitud) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(solicitud.username(), solicitud.password()));
        var usuario = usuarios.buscarPorCorreo(CorreoElectronico.of(solicitud.username()))
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Correo o contraseña incorrectos."));
        var token = jwtService.generar(usuario.getId().valor(), usuario.getCorreo().direccion(), usuario.getRol());
        return new TokenResponse(token.token(), "Bearer", token.expireAt(), token.roles());
    }

    @PostMapping("/registro")
    public ResponseEntity<TokenResponse> registrar(@Valid @RequestBody RegistroRequest solicitud) {
        var creado = crearUsuario.ejecutar(new CrearUsuarioUseCase.Command(solicitud.nombre(), solicitud.apellido(),
                solicitud.documento(), solicitud.correo(), solicitud.telefono(), solicitud.password(),
                RolUsuario.HUESPED, solicitud.fechaNacimiento(), null));
        var usuario = usuarios.buscarPorId(UsuarioId.of(creado.id())).orElseThrow();
        var token = jwtService.generar(usuario.getId().valor(), usuario.getCorreo().direccion(), usuario.getRol());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(token.token(), "Bearer", token.expireAt(), token.roles()));
    }
}
