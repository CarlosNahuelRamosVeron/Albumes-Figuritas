package com.tp.album.service.strategy.impl;

import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.enumeration.Rareza;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Component("uniforme")
@Service
public class UniformeImpl implements DistributionStrategy {
    private final Random random = new Random();
    private final Rareza[] niveles = Rareza.values();

    @Override
    public void asignarRarezaYStock(Figurita figurita, int defaultStockPorFigurita) {
        Rareza r = niveles[random.nextInt(niveles.length)];
        figurita.setRareza(r);
        int multiplicador = (r == Rareza.COMUN) ? 1 : (r == Rareza.RARA) ? 1 : 1;
        figurita.setStockTotal(defaultStockPorFigurita / multiplicador);
        figurita.setStockDisponible(figurita.getStockTotal());
    }
}
