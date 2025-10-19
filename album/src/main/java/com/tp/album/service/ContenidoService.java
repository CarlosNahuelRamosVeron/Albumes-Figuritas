package com.tp.album.service;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.service.factory.ContenidoFactory;
import org.springframework.stereotype.Service;

import com.tp.album.model.entities.Album;
import com.tp.album.service.strategy.DistributionStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    public List<Contenido> creaContenidos(Album album, List<ContenidoDTO> contenidosDTO, DistributionStrategy strategy, int defaultStock) {
        return contenidosDTO.stream()
                .map(dto -> ContenidoFactory
                    .getFactory(dto, strategy, defaultStock)
                    .crearDesdeDTO(dto, album)
                ).collect(Collectors.toList());
    }

}
