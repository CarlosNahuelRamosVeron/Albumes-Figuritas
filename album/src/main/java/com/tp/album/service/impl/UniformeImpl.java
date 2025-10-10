package com.tp.album.service.impl;

import com.tp.album.entities.Rarity;
import com.tp.album.entities.Sticker;
import com.tp.album.service.DistributionStrategy;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Component("uniforme")
@Service
public class UniformeImpl implements DistributionStrategy {
    private final Random random = new Random();
    private final Rarity[] niveles = Rarity.values();

    @Override
    public void asignarRarityAndStock(List<Sticker> stickers, int defaultStockPerSticker) {
        for (Sticker s : stickers) {
            Rarity r = niveles[random.nextInt(niveles.length)];
            s.setRarity(r);
            int multiplier = (r == Rarity.COMUN) ? 1 : (r == Rarity.RARA) ? 1 : 1;
            s.setStockTotal(defaultStockPerSticker / multiplier);
            s.setStockDisponible(s.getStockTotal());
        }
    }
}
