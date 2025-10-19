package com.tp.album.model.entities;

import com.tp.album.model.enumeration.UsuarioRole;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private UsuarioRole role; //ADMIN OR USER
}