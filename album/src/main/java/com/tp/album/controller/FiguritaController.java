package com.tp.album.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.entities.Figurita;
import com.tp.album.service.impl.AlbumService;

@RestController
@RequestMapping("/figurita")
public class FiguritaController {

    private final AlbumService albumService;
    //private final DistributionStrategy strategy;

    public FiguritaController(AlbumService albumService) { //@Qualifier("ponderado") DistributionStrategy strategy
        this.albumService = albumService;
        //this.strategy = strategy;
    }

    @GetMapping("/obtener-figuritas/{albumId}")
    public ResponseEntity<List<Figurita>> obetenerFiguritas(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.obtenerFiguritas(albumId));
    }

    @PostMapping("/{albumId}/figuritas")
    public ResponseEntity<List<Figurita>> cargarFiguritas(
            @PathVariable Long albumId,
            @RequestBody List<CargarFiguritaDTO> cargarFiguritaDTOs,
            @RequestParam(defaultValue = "automatico") String modo) {

        List<Figurita> figuritas = albumService.cargarFiguritas(albumId, cargarFiguritaDTOs, modo);
        return ResponseEntity.ok(figuritas);
    }
    
}
