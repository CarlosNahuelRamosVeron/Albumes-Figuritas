package com.tp.album.model.dto;

import com.tp.album.model.entities.Album;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlbumResponseDTO extends AlbumSummaryDTO {
    private List<ContenidoResponseDTO> contenidos;

    public AlbumResponseDTO(Album album) {
        super(album);

    }
}

