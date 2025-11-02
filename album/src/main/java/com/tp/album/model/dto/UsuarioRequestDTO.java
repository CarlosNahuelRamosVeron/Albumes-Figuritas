package com.tp.album.model.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String username;
    private String password;
    // ADMIN o USER (solo aplicable si quien actualiza es ADMIN)
    private String role;
}

