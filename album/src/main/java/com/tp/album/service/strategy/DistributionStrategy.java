package com.tp.album.service.strategy;

import com.tp.album.model.entities.Figurita;

public interface DistributionStrategy {
    void asignarRarezaYStock(Figurita figurita, int defaultStockPorFigurita);
}