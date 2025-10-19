package com.tp.album.service;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.dto.CrearUsuarioDTO;
import com.tp.album.model.entities.Usuario;
import com.tp.album.model.enumeration.UsuarioRole;
import com.tp.album.model.repository.UsuarioRepository;

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

    public Usuario actualizarUsuario(Long id, Usuario datosActualizados) {
        Usuario existente = obtenerUsuarioPorId(id);
        existente.setUsername(datosActualizados.getUsername());
        if (datosActualizados.getRole() != null) {
            existente.setRole(datosActualizados.getRole());
        }
        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    public void eliminarUsuarioPorId(Long id) {
        usuarioRepository.deleteById(id);
    }


}