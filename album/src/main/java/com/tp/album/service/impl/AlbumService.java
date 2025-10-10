package com.tp.album.service.impl;

import com.tp.album.entities.Album;
import com.tp.album.entities.Rarity;
import com.tp.album.entities.Sticker;
import com.tp.album.dto.UploadStickerDTO;
import com.tp.album.repository.AlbumRepository;
import com.tp.album.repository.StickerRepository;
import com.tp.album.service.DistributionStrategy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final StickerRepository stickerRepository;
    private final StickerService stickerService;
    private final ImageService imageService;

    public AlbumService(AlbumRepository albumRepository,
                        StickerRepository stickerRepository,
                        StickerService stickerService,
                        ImageService imageService) {
        this.albumRepository = albumRepository;
        this.stickerRepository = stickerRepository;
        this.stickerService = stickerService;
        this.imageService = imageService;
    }

    public Album createAlbum(Album album) {
        album.setPublicado(false);
        return albumRepository.save(album);
    }

    @Transactional
    public List<Sticker> cargarStickers(Long albumId,
                                        List<UploadStickerDTO> metadata,
                                        List<MultipartFile> images,
                                        DistributionStrategy strategy) throws Exception {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));

        if (images.size() != metadata.size()) {
            throw new IllegalArgumentException("Metadata e imágenes deben coincidir");
        }            

        // Guardar imagenes y actualizar la url
        for (int i = 0; i < images.size(); i++) {
            String url = imageService.saveAndValidateAlbumImage(albumId, images.get(i), Paths.get("uploads"));
            metadata.get(i).setUrl(url);
        }

        // Crear stickers
        List<Sticker> stickers = stickerService.createStickers(album, metadata, strategy, 100);

        // Guardar stickers 
        for (Sticker s : stickers) {
            stickerRepository.save(s);
            album.addSticker(s);
        }

        // Guardar album
        albumRepository.save(album);

        return stickers;
    }

    @Transactional
    public Album publicarAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow();
        // calcular dificultad por rareza promedio
        double score = album.getStickers().stream()
            .mapToDouble(s -> 
                s.getRarity() == Rarity.COMUN ? 1 
                    : s.getRarity() == Rarity.RARA ? 2 
                        : 3
            )
            .average().orElse(1.0);
        if (score < 1.5) {
            album.setDificultad("FACIL");
        } else if (score < 2.5) {
            album.setDificultad("MEDIO");
        } else {
            album.setDificultad("DIFICIL");
        }
        album.setPublicado(true);
        return albumRepository.save(album);
    }
}