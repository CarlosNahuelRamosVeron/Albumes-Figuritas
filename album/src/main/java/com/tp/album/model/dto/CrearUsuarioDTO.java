package com.tp.album.model.dto;

import lombok.Data;

@Data
public class CrearUsuarioDTO {
    private String username;
    private String password;
    private String role; //ADMIN OR USER
}
