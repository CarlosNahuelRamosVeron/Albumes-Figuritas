package com.tp.album.service.factory;

import com.tp.album.model.dto.CargarSeccionDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Seccion;
import com.tp.album.service.strategy.DistributionStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class SeccionFactory extends ContenidoFactory {

    public SeccionFactory(DistributionStrategy strategy, int defaultStock) {
        super(strategy, defaultStock);
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
                .map(contenidoDTO -> ContenidoFactory.getFactory(contenidoDTO, this.getStrategy(), this.getDefaultStock()).crearDesdeDTO(contenidoDTO, album))
                .collect(Collectors.toList());
        contenidosHijos.forEach(contenido -> {
            contenido.setParent(seccion);
        });
        seccion.setContenidos(contenidosHijos);
    }
}

