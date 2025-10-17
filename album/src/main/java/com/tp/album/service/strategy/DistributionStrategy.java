package com.tp.album.service.strategy;

import java.util.List;

import com.tp.album.model.entities.Figurita;

public interface DistributionStrategy {
    void asignarRarezaYStock(List<Figurita> figuritas, int defaultStockPorFigurita);
}