package com.tp.album.service.strategy;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.service.validation.ImageValidation;
import lombok.Getter;

@Getter
public abstract class CreadorContenidoStrategy {

    private DistributionStrategy strategy;
    private int defaultStock;
    private ImageValidation imageValidation;

    public CreadorContenidoStrategy(DistributionStrategy strategy, int defaultStock, ImageValidation imageValidation) {
        this.strategy = strategy;
        this.defaultStock = defaultStock;
        this.imageValidation = imageValidation;
    }

    public abstract Contenido crearDesdeDTO(ContenidoDTO dto, Album album);

}
