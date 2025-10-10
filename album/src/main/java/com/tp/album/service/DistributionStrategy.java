package com.tp.album.service;

import java.util.List;

import com.tp.album.entities.Sticker;

public interface DistributionStrategy {
    void asignarRarityAndStock(List<Sticker> stickers, int defaultStockPerSticker);
}