package com.tp.album.model.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CargarFiguritaDTO extends ContenidoDTO {
    @Schema(example = "FIGURITA")
    private final String tipo = "FIGURITA";
    private int numero;
    private String imagenBase64;

    @Override
    public String getTipo() {
        return tipo;
    }
}