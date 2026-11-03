package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.*;

/** Adaptador JPA del repositorio de usuarios. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class UsuarioJpaRepository implements UsuarioRepository {
    private final UsuarioJpaDataRepository dataRepository;
    private final UsuarioPersistenceMapper mapper;
    /** Guarda o actualiza un usuario. */
    public void guardar(Usuario u) { dataRepository.save(mapper.aEntidad(u)); }
    /** Busca un usuario por identificador. */
    @Transactional(readOnly=true) public Optional<Usuario> buscarPorId(UsuarioId id) { return dataRepository.findById(id.valor().toString()).map(mapper::aDominio); }
    /** Busca un usuario por correo electrónico. */
    @Transactional(readOnly=true) public Optional<Usuario> buscarPorCorreo(CorreoElectronico correo) { return dataRepository.findByCorreo(correo.direccion()).map(mapper::aDominio); }
    /** Indica si el correo ya está registrado. */
    @Transactional(readOnly=true) public boolean existePorCorreo(CorreoElectronico correo) { return dataRepository.findByCorreo(correo.direccion()).isPresent(); }
}
