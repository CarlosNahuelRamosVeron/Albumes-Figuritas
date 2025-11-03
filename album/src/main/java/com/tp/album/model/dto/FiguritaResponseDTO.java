package com.tp.album.model.dto;

import com.tp.album.model.enumeration.Rareza;
import lombok.Data;

@Data
public class FiguritaResponseDTO extends ContenidoResponseDTO {
    private final String tipo = "FIGURITA";
    private int numero;
    private Rareza rareza;
    private Integer stockTotal;
    private Integer stockDisponible;
    private String urlImagen;

    @Override
    public String getTipo() {
        return tipo;
    }
}

