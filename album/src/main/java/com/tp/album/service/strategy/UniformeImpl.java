package com.tp.album.service.strategy;

import com.tp.album.model.entities.Rareza;
import com.tp.album.model.entities.Figurita;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Component("uniforme")
@Service
public class UniformeImpl implements DistributionStrategy {
    private final Random random = new Random();
    private final Rareza[] niveles = Rareza.values();

    @Override
    public void asignarRarezaYStock(List<Figurita> figuritas, int defaultStockPorFigurita) {
        for (Figurita figurita : figuritas) {
            Rareza r = niveles[random.nextInt(niveles.length)];
            figurita.setRareza(r);
            int multiplicador = (r == Rareza.COMUN) ? 1 : (r == Rareza.RARA) ? 1 : 1;
            figurita.setStockTotal(defaultStockPorFigurita / multiplicador);
            figurita.setStockDisponible(figurita.getStockTotal());
        }
    }
}
