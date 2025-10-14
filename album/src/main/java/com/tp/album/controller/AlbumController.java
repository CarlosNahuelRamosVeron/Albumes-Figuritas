package com.tp.album.controller;

import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.service.impl.AlbumService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    //get all
    @GetMapping
    public ResponseEntity<List<Album>> obtenerAlbumes() {
        return ResponseEntity.ok(albumService.obtenerAlbums());
    }

    //get by id
    @GetMapping("/{albumId}")
    public ResponseEntity<Album> obetenerAlbumPorId(@PathVariable Long albumId) {
        try {
            Album album = albumService.obtenerAlbumPorId(albumId);
            return ResponseEntity.ok(album);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<Album> publicar(@PathVariable Long id) {
        Album albumGuardado = albumService.publicarAlbum(id);
        return ResponseEntity.ok(albumGuardado);
    }

    //create
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> crearAlbum(@Valid @RequestBody CrearAlbumDTO dto) {
        Album albumGuardado = albumService.crearAlbum(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumGuardado);
    }

    //delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAlbum(@PathVariable Long id) {
        try {
            albumService.eliminarAlbum(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> actualizarAlbum(@PathVariable Long id,
                                                 @Valid @RequestBody CrearAlbumDTO dto) {
        try {
            Album albumActualizado = albumService.actualizarAlbum(id, dto);
            return ResponseEntity.ok(albumActualizado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
