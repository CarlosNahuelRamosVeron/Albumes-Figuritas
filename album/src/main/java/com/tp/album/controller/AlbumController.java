package com.tp.album.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.album.dto.CreateAlbumDTO;
import com.tp.album.dto.UploadStickerDTO;
import com.tp.album.entities.Album;
import com.tp.album.entities.Sticker;
import com.tp.album.service.DistributionStrategy;
import com.tp.album.service.impl.AlbumService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;
    private final ObjectMapper objectMapper;
    private final DistributionStrategy strategy;

     public AlbumController(AlbumService albumService, ObjectMapper objectMapper,
                        @Qualifier("ponderado") DistributionStrategy strategy) {
        this.albumService = albumService;
        this.objectMapper = objectMapper;
        this.strategy = strategy;
    }

    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestBody CreateAlbumDTO dto) {
        Album album = new Album();
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        Album saved = albumService.createAlbum(album);
        return ResponseEntity.ok(saved);
    }

    // multipart/form-data: images + metadata (JSON string field)
    /*
     * # metadata.json es un array JSON: [{"name":"A1","number":1},{"name":"A2","number":2},...]
        curl -X POST "http://localhost:8080/admin/albums/1/stickers" \
            -F "metadata=@metadata.json;type=application/json" \
            -F "images=@A1.png" \
            -F "images=@A2.png"
     */
    @PostMapping("/{id}/stickers")
    public ResponseEntity<List<Sticker>> uploadStickers(@PathVariable Long id,
                                                    @RequestPart("metadata") String metadataJson,
                                                    @RequestPart("images") List<MultipartFile> images) throws Exception {
        List<UploadStickerDTO> metadata = objectMapper.readValue(metadataJson, new TypeReference<List<UploadStickerDTO>>() {});
        List<Sticker> stickers = albumService.cargarStickers(id, metadata, images, strategy);
        return ResponseEntity.ok(stickers);
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<Album> publicar(@PathVariable Long id) {
        Album a = albumService.publicarAlbum(id);
        return ResponseEntity.ok(a);
    }
}
