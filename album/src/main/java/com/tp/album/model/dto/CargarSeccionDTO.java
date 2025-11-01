package com.tp.album.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CargarSeccionDTO extends  ContenidoDTO {
    private final String tipo = "SECCION";
    private String descripcion;
    private List<ContenidoDTO> contenidos;

    @Override
    public String getTipo() {
        return tipo;
    }
}