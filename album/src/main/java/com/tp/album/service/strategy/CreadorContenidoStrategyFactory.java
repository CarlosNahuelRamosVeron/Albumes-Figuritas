package com.tp.album.service.strategy;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.service.strategy.impl.CreadorFiguritaStrategy;
import com.tp.album.service.strategy.impl.CreadorSeccionStrategy;
import com.tp.album.service.validation.ImageValidation;


public  class CreadorContenidoStrategyFactory {

    public static CreadorContenidoStrategy getFactory(ContenidoDTO dto, DistributionStrategy strategy, int defaultStock, ImageValidation imageValidation) {
        switch (dto.getTipo()) {
            case "FIGURITA": return new CreadorFiguritaStrategy(strategy, defaultStock, imageValidation);
            case "SECCION": return new CreadorSeccionStrategy(strategy, defaultStock, imageValidation);
            default: throw new IllegalArgumentException("Tipo de contenido no soportado: " + dto.getTipo());
        }
    }


}

