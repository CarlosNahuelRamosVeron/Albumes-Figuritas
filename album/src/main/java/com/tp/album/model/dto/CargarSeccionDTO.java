package com.tp.album.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CargarSeccionDTO implements ContenidoDTO {
    private final String tipo = "SECCION";
    private String nombre;
    private String descripcion;
    private List<ContenidoDTO> contenidos;

    @Override
    public String getTipo() {
        return tipo;
    }
}