package com.tp.album.service;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.dto.ActualizarUsuarioDTO;
import com.tp.album.model.dto.CrearUsuarioDTO;
import com.tp.album.model.entities.Usuario;
import com.tp.album.model.enumeration.UsuarioRole;
import com.tp.album.model.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new SecurityUser(user);
    }

    public Usuario crearUsuario(CrearUsuarioDTO crearUsuarioDTO) throws Exception {
        Optional<Usuario> usuarioOptional = obtenerUsuarioPorUsername(crearUsuarioDTO.getUsername());
        if (usuarioOptional.isEmpty()) {
            crearUsuarioDTO.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));
            UsuarioRole role = UsuarioRole.valueOf(crearUsuarioDTO.getRole());
            Usuario usuario = new Usuario();
            usuario.setUsername(crearUsuarioDTO.getUsername());
            usuario.setPassword(crearUsuarioDTO.getPassword());
            usuario.setRole(role);
            return usuarioRepository.save(usuario);
        } else {
            throw new Exception("El nombre de usuario ya existe, intente con otro.");
        }
    }

    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuario(ActualizarUsuarioDTO datosActualizados) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.obtenerUsuarioPorId(datosActualizados.getId());
        validarPermisoAdminOMismoUsuario(usuario, auth);

        usuario.setUsername(datosActualizados.getUsername());
        if (datosActualizados.getRole() != null) {
            UsuarioRole nuevoRol = UsuarioRole.valueOf(datosActualizados.getRole().trim().toUpperCase());
            usuario.setRole(nuevoRol);
        }
        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuarioPorId(Long id) {
        Usuario usuario = this.obtenerUsuarioPorId(id);
        validarPermisoAdminOMismoUsuario(usuario, SecurityContextHolder.getContext().getAuthentication());
        usuarioRepository.delete(usuario);
    }

    private void validarPermisoAdminOMismoUsuario(Usuario usuario, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }
        String requester = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isSelf = requester != null && requester.equals(usuario.getUsername());
        if (!isSelf && !isAdmin) {
            throw new AccessDeniedException("No tiene permisos para modificar este usuario");
        }
    }

}