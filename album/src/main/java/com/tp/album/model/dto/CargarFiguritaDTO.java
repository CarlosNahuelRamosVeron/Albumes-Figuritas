package com.tp.album.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CargarFiguritaDTO implements ContenidoDTO {
    private final String tipo = "FIGURITA";
    private String nombre;
    private int numero;
    private MultipartFile archivoImagen;

    @Override
    public String getTipo() {
        return tipo;
    }
}