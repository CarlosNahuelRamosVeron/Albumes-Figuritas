package com.tp.album.service;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.service.factory.ContenidoFactory;
import com.tp.album.service.validation.ImageValidation;
import org.springframework.stereotype.Service;

import com.tp.album.model.entities.Album;
import com.tp.album.service.strategy.DistributionStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    private final ImageValidation imageValidator;

    public ContenidoService(ImageValidation imageValidator) {
        this.imageValidator = imageValidator;
    }

    public List<Contenido> creaContenidos(Album album,
                                          List<ContenidoDTO> contenidosDTO,
                                          DistributionStrategy strategy,
                                          int defaultStock) {
        return contenidosDTO.stream()
                .map(dto -> {
                    ContenidoFactory factory = ContenidoFactory.getFactory(dto, strategy, defaultStock);

                    if ("FIGURITA".equals(dto.getTipo())) {
                        return factory.crearDesdeDTO(dto, album, imageValidator);
                    } else {
                        return factory.crearDesdeDTO(dto, album, null);
                    }
                })
                .collect(Collectors.toList());
    }
}