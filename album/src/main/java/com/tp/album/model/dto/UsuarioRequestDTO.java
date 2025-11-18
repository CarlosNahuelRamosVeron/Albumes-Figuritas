package com.tp.album.model.dto;

import com.tp.album.model.entities.Usuario;
import com.tp.album.model.enumeration.UsuarioRole;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class UsuarioRequestDTO {
    private String username;
    private String password;
    private UsuarioRole role;

    public Usuario toEntity(PasswordEncoder passwordEncoder) {
        Usuario usuario = new Usuario();
        usuario.setUsername(this.username);
        usuario.setPassword(passwordEncoder.encode(this.password));
        usuario.setRole(this.role);
        return usuario;
    }
}

