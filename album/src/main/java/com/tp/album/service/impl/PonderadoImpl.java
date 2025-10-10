package com.tp.album.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.tp.album.entities.Rarity;
import com.tp.album.entities.Sticker;
import com.tp.album.service.DistributionStrategy;

import java.util.List;
import java.util.Random;

@Component("ponderado")
@Service
public class PonderadoImpl implements DistributionStrategy {
    private final Random random = new Random();

    // ej: 0.7 commons, 0.25 rare, 0.05 epic
    @Override
    public void asignarRarityAndStock(List<Sticker> stickers, int defaultStockPerSticker) {
        for (Sticker s : stickers) {
            double r = random.nextDouble();
            if (r < 0.7) {
                s.setRarity(Rarity.COMUN);
                s.setStockTotal(defaultStockPerSticker);
            } else if (r < 0.95) {
                s.setRarity(Rarity.RARA);
                s.setStockTotal(Math.max(1, defaultStockPerSticker / 2));
            } else {
                s.setRarity(Rarity.EPICA);
                s.setStockTotal(Math.max(1, defaultStockPerSticker / 5));
            }
            s.setStockDisponible(s.getStockTotal());
        }
    }
}
