package com.tp.album.model.dto;

import lombok.Data;

@Data
public class CargarFiguritaDTO implements ContenidoDTO {
    private final String tipo = "FIGURITA";
    private String nombre;
    private int numero;
    private String url;

    @Override
    public String getTipo() {
        return tipo;
    }
}