package com.tp.album.service.strategy.impl;

import com.tp.album.model.dto.CargarSeccionDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Seccion;
import com.tp.album.service.strategy.CreadorContenidoStrategy;
import com.tp.album.service.strategy.CreadorContenidoStrategyFactory;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.validation.ImageValidation;

import java.util.List;
import java.util.stream.Collectors;

public class CreadorSeccionStrategy extends CreadorContenidoStrategy {

    public CreadorSeccionStrategy(DistributionStrategy strategy, int defaultStock, ImageValidation imageValidation) {
        super(strategy, defaultStock, imageValidation);
    }

    @Override
    public Seccion crearDesdeDTO(ContenidoDTO dto, Album album) {
        CargarSeccionDTO seccionDTO = (CargarSeccionDTO) dto;
        Seccion seccion = new Seccion();
        seccion.setNombre(seccionDTO.getNombre());
        seccion.setAlbum(album);

        if (seccionDTO.getContenidos() != null && !seccionDTO.getContenidos().isEmpty()) {
            agregarContenido(album, seccionDTO, seccion);
        }
        return seccion;
    }
    private void agregarContenido(Album album, CargarSeccionDTO seccionDTO, Seccion seccion) {
        List<Contenido> contenidosHijos = seccionDTO.getContenidos().stream()
                .map(contenidoDTO -> CreadorContenidoStrategyFactory.getFactory(contenidoDTO, this.getStrategy(), this.getDefaultStock(), this.getImageValidation()).crearDesdeDTO(contenidoDTO, album))
                .collect(Collectors.toList());
        contenidosHijos.forEach(contenido -> contenido.setParent(seccion));
        seccion.setContenidos(contenidosHijos);
    }
}

