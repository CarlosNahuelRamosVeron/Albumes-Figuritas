package com.tp.album.service.factory;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.service.strategy.DistributionStrategy;

import lombok.Data;

@Data
public abstract class ContenidoFactory {

    private DistributionStrategy strategy;
    private int defaultStock;

    public ContenidoFactory(DistributionStrategy strategy, int defaultStock) {
        this.strategy = strategy;
        this.defaultStock = defaultStock;
    }

    public abstract Contenido crearDesdeDTO(ContenidoDTO dto, Album album);

    public static ContenidoFactory getFactory(ContenidoDTO dto, DistributionStrategy strategy, int defaultStock) {
        switch (dto.getTipo()) {
            case "FIGURITA": return new FiguritaFactory(strategy, defaultStock);
            case "SECCION": return new SeccionFactory(strategy, defaultStock);
            default: throw new IllegalArgumentException("Tipo de contenido no soportado: " + dto.getTipo());
        }
    }

}

