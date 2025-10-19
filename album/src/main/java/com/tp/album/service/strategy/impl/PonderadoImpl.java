package com.tp.album.service.strategy.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.enumeration.Rareza;

import java.util.Random;

@Component("ponderado")
@Service
public class PonderadoImpl implements DistributionStrategy {
    private final Random random = new Random();

    // por ejemplo: 0.7 comun, 0.25 rara, 0.05 epica
    @Override
    public void asignarRarezaYStock(Figurita figurita, int defaultStockPorFigurita) {
            double r = random.nextDouble();
            if (r < 0.7) {
                figurita.setRareza(Rareza.COMUN);
                figurita.setStockTotal(defaultStockPorFigurita);
            } else if (r < 0.95) {
                figurita.setRareza(Rareza.RARA);
                figurita.setStockTotal(Math.max(1, defaultStockPorFigurita / 2));
            } else {
                figurita.setRareza(Rareza.EPICA);
                figurita.setStockTotal(Math.max(1, defaultStockPorFigurita / 5));
            }
            figurita.setStockDisponible(figurita.getStockTotal());
    }
}
