package com.tp.album.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CargaContenidoResponseDTO {
    private AlbumSummaryDTO album;
    private List<ContenidoResponseDTO> contenidos;

    public CargaContenidoResponseDTO(AlbumSummaryDTO albumDTO, List<ContenidoResponseDTO> contenidosDTO) {
        this.album = albumDTO;
        this.contenidos = contenidosDTO;
    }
}

