package com.tp.album.model.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String username;
    private String password;
    private String role;
}

