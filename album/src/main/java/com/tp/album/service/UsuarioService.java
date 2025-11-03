package com.tp.album.service;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.dto.UsuarioRequestDTO;
import com.tp.album.model.entities.Usuario;
import com.tp.album.model.enumeration.UsuarioRole;
import com.tp.album.model.repository.UsuarioRepository;

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
import java.util.NoSuchElementException;
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
    public UserDetails loadUserByUsername(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new SecurityUser(user);
    }

    public Usuario crearUsuario(UsuarioRequestDTO dto) {
        Optional<Usuario> usuarioOptional = obtenerUsuarioPorUsername(dto.getUsername());
        if (usuarioOptional.isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            UsuarioRole role = UsuarioRole.valueOf(dto.getRole());
            Usuario usuario = new Usuario();
            usuario.setUsername(dto.getUsername());
            usuario.setPassword(dto.getPassword());
            usuario.setRole(role);
            return usuarioRepository.save(usuario);
        } else {
            throw new IllegalArgumentException("El nombre de usuario ya existe, intente con otro.");
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
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.obtenerUsuarioPorId(id);
        validarPermisoAdminOMismoUsuario(usuario, auth);

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            usuario.setUsername(dto.getUsername());
        }
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            UsuarioRole nuevoRol = UsuarioRole.valueOf(dto.getRole().trim().toUpperCase());
            usuario.setRole(nuevoRol);
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
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