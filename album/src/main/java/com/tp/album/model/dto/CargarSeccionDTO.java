package com.tp.album.model.dto;

import lombok.Data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CargarSeccionDTO extends  ContenidoDTO {
    @Schema(example = "SECCION")
    private final String tipo = "SECCION";
    private String descripcion;
    private List<ContenidoDTO> contenidos;

    @Override
    public String getTipo() {
        return tipo;
    }
}