package com.tp.album.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.entities.Figurita;
import com.tp.album.service.DistributionStrategy;
import com.tp.album.service.impl.AlbumService;

@RestController
@RequestMapping("/figurita")
public class FiguritaController {

    private final AlbumService albumService;
    private final DistributionStrategy strategy;

    public FiguritaController(AlbumService albumService,
                            @Qualifier("ponderado") DistributionStrategy strategy) {
        this.albumService = albumService;
        this.strategy = strategy;
    }

    @GetMapping("/obtener-figuritas/{albumId}")
    public ResponseEntity<List<Figurita>> obetenerFiguritas(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.obetenerFiguritas(albumId));
    }

    @PostMapping("/{albumId}/figuritas")
    public ResponseEntity<List<Figurita>> cargarFiguritas(@PathVariable Long albumId,
                                                    @RequestBody List<CargarFiguritaDTO> cargarFiguritaDTOs) {
        List<Figurita> figuritas = albumService.cargarFiguritas(albumId, cargarFiguritaDTOs, strategy);
        return ResponseEntity.ok(figuritas);
    }
    
}
