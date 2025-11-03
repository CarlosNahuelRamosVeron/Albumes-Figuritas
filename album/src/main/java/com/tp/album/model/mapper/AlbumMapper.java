package com.tp.album.model.mapper;

import com.tp.album.model.dto.AlbumResponseDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    private ContenidoMapper contenidoMapper;

    public  AlbumMapper(ContenidoMapper contenidoMapper) {
        this.contenidoMapper = contenidoMapper;
    }

    public AlbumResponseDTO toAlbumResponseDTO(Album albumActualizado, boolean incluirContenidos) {
        if (albumActualizado == null) return null;
        AlbumResponseDTO dto = new AlbumResponseDTO(albumActualizado);
        if (incluirContenidos && albumActualizado.getContenidos() != null) {
            List<Contenido> contenidosRaices = albumActualizado.getContenidos().stream().filter(Contenido::isRoot).collect(Collectors.toList());
            dto.setContenidos(contenidoMapper.toContenidoResponseDTOList(contenidosRaices));
        }
        return dto;
    }
}
