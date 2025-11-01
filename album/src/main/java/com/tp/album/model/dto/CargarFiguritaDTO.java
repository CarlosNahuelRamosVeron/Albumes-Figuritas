package com.tp.album.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CargarFiguritaDTO extends ContenidoDTO {
    @Schema(example = "FIGURITA")
    private final String tipo = "FIGURITA";
    private int numero;
    private MultipartFile archivoImagen;

    @Override
    public String getTipo() {
        return tipo;
    }
}