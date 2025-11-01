package com.tp.album.model.dto;

import lombok.Data;

@Data
public class ActualizarUsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private String role; // ADMIN o USER (solo aplicable si quien actualiza es ADMIN)
}

