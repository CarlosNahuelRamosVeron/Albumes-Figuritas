package com.tp.album.model.dto;

import com.tp.album.model.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String role;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.role = usuario.getRole() != null ? usuario.getRole().name() : null;
    }
}

