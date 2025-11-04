package com.tp.album.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SeccionResponseDTO extends ContenidoResponseDTO {
    private final String tipo = "SECCION";
    private List<ContenidoResponseDTO> contenidos = new ArrayList<>();

    @Override
    public String getTipo() {
        return tipo;
    }
}

