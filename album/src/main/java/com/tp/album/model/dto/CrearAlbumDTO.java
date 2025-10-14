package com.tp.album.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearAlbumDTO {
    @NotBlank
    private String titulo;
    private String descripcion;
    @NotBlank
    private String categoria;
}
