package com.tp.album.service.factory;

import com.tp.album.model.dto.CargarSeccionDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Seccion;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.validation.ImageValidation;

import java.util.List;
import java.util.stream.Collectors;

public class SeccionFactory extends ContenidoFactory {

    public SeccionFactory(DistributionStrategy strategy, int defaultStock) {
        super(strategy, defaultStock);
    }

    @Override
    public Seccion crearDesdeDTO(ContenidoDTO dto, Album album, ImageValidation validator) {
        CargarSeccionDTO seccionDTO = (CargarSeccionDTO) dto;
        Seccion seccion = new Seccion();
        seccion.setNombre(seccionDTO.getNombre());
        seccion.setAlbum(album);

        if (seccionDTO.getContenidos() != null && !seccionDTO.getContenidos().isEmpty()) {
            List<Contenido> contenidosHijos = seccionDTO.getContenidos().stream()
                    .map(contenidoDTO -> {
                        ContenidoFactory factory = ContenidoFactory.getFactory(
                                contenidoDTO,
                                this.getStrategy(),
                                this.getDefaultStock());
                        if ("FIGURITA".equals(contenidoDTO.getTipo())) {
                            return factory.crearDesdeDTO(contenidoDTO, album, validator);
                        } else {
                            return factory.crearDesdeDTO(contenidoDTO, album, null);
                        }
                    })
                    .collect(Collectors.toList());

            contenidosHijos.forEach(c -> c.setParent(seccion));
            seccion.setContenidos(contenidosHijos);
        }

        return seccion;
    }
}

