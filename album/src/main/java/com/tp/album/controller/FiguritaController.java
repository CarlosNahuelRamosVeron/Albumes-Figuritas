package com.tp.album.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.album.dto.CargarFiguritaDTO;
import com.tp.album.entities.Figurita;
import com.tp.album.service.DistributionStrategy;
import com.tp.album.service.impl.AlbumService;

@RestController
@RequestMapping("/figurita")
public class FiguritaController {

    private final AlbumService albumService;
    private final ObjectMapper objectMapper;
    private final DistributionStrategy strategy;

    public FiguritaController(AlbumService albumService, 
                            ObjectMapper objectMapper,
                            @Qualifier("ponderado") DistributionStrategy strategy) {
        this.albumService = albumService;
        this.objectMapper = objectMapper;
        this.strategy = strategy;
    }

    @GetMapping("/obtener-figuritas/{albumId}")
    public ResponseEntity<List<Figurita>> obetenerFiguritas(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.obetenerFiguritas(albumId));
    }

    // multipart/form-data: images + metadata (JSON string field)
    /*
     * # metadata.json es un array JSON: [{"name":"A1","number":1},{"name":"A2","number":2},...]
        curl -X POST "http://localhost:8080/album/1/figuritas" \
            -F "metadata=@metadata.json;type=application/json" \
            -F "images=@A1.png" \
            -F "images=@A2.png"
     */
    @PostMapping("/{id}/figuritas")
    public ResponseEntity<List<Figurita>> cargarFiguritas(@PathVariable Long id,
                                                    @RequestPart("metadata") String metadataJson,
                                                    @RequestPart("images") List<MultipartFile> imagenes) throws Exception {
        List<CargarFiguritaDTO> metadata = objectMapper.readValue(metadataJson, new TypeReference<List<CargarFiguritaDTO>>() {});
        List<Figurita> figuritas = albumService.cargarFiguritas(id, metadata, imagenes, strategy);
        return ResponseEntity.ok(figuritas);
    }
    
}
