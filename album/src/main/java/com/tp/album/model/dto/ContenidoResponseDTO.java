package com.tp.album.model.dto;

import lombok.Data;

@Data
public abstract class ContenidoResponseDTO {
    private Long id;
    private String nombre;
    private Long parentId;

    public abstract String getTipo();
}

