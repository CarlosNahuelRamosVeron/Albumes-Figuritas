package com.tp.album.service.impl;

import org.springframework.stereotype.Service;

import com.tp.album.dto.UploadStickerDTO;
import com.tp.album.entities.Album;
import com.tp.album.entities.Sticker;
import com.tp.album.service.DistributionStrategy;

import java.util.ArrayList;
import java.util.List;

@Service
public class StickerService {

    public List<Sticker> createStickers(Album album,
                                    List<UploadStickerDTO> uploads,
                                    DistributionStrategy strategy,
                                    int defaultStock) {
        List<Sticker> stickers = new ArrayList<>();
        //int num = 1;
        for (UploadStickerDTO dto : uploads) {
            Sticker s = new Sticker();
            s.setNombre(dto.getNombre());
            s.setNumero(dto.getNumero());
            s.setAlbum(album);
            s.setUrl(dto.getUrl()); // imageUrl set by ImageService
            stickers.add(s);
        }
        strategy.asignarRarityAndStock(stickers, defaultStock);
        return stickers;
    }
}
