package com.tp.album.service;

import java.util.List;

import com.tp.album.entities.Figurita;

public interface DistributionStrategy {
    void asignarRarezaYStock(List<Figurita> figuritas, int defaultStockPorFigurita);
}