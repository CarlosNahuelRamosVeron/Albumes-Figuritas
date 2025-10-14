package com.tp.album.service.impl;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.entities.Usuario;
import com.tp.album.model.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new SecurityUser(user);
    }

    public Usuario crearUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
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
        existente.setRole(datosActualizados.getRole());
        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }


}