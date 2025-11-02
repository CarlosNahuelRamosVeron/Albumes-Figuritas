package com.tp.album.model.mapper;

import com.tp.album.model.dto.*;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.entities.Seccion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContenidoMapper {

    public CargaContenidoResponseDTO toCargaContenidoResponseDTO(List<Contenido> contenidos) {
        AlbumSummaryDTO albumDTO = new AlbumSummaryDTO(contenidos.get(0).getAlbum());
        List<ContenidoResponseDTO> contenidosDTO = this.toContenidoResponseDTOList(contenidos.stream().filter(Contenido::isRoot).collect(Collectors.toList()));
        return new CargaContenidoResponseDTO(albumDTO, contenidosDTO);
    }

    public List<ContenidoResponseDTO> toContenidoResponseDTOList(List<Contenido> contenidos) {
        return  contenidos.stream()
                .map(this::toContenidoResponseDTO)
                .collect(Collectors.toList());
    }

    public ContenidoResponseDTO toContenidoResponseDTO(Contenido contenido) {
        if (contenido instanceof Figurita) {
            return toFiguritaResponseDTO((Figurita) contenido);
        } else if (contenido instanceof Seccion) {
            return toSeccionResponseDTO((Seccion) contenido);
        } else {
            throw new IllegalArgumentException("Tipo de contenido no soportado: " + contenido.getClass());
        }
    }

    private FiguritaResponseDTO toFiguritaResponseDTO(Figurita f) {
        FiguritaResponseDTO dto = new FiguritaResponseDTO();
        dto.setId(f.getId());
        dto.setNombre(f.getNombre());
        dto.setRareza(f.getRareza());
        dto.setNumero(f.getNumero());
        dto.setStockTotal(f.getStockTotal());
        dto.setStockDisponible(f.getStockDisponible());
        dto.setParentId(f.getParent() != null ? f.getParent().getId() : null);
        return dto;
    }

    private SeccionResponseDTO toSeccionResponseDTO(Seccion s) {
        SeccionResponseDTO dto = new SeccionResponseDTO();
        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setParentId(s.getParent() != null ? s.getParent().getId() : null);
        if (s.getContenidos() != null) {
            List<ContenidoResponseDTO> hijos = this.toContenidoResponseDTOList(s.getContenidos());
            dto.setContenidos(hijos);
        }
        return dto;
    }

}
