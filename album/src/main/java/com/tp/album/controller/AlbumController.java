package com.tp.album.controller;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.service.AlbumService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping
    public ResponseEntity<List<Album>> obtenerAlbumes() {
        return ResponseEntity.ok(albumService.obtenerAlbumes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> obtenerAlbumPorId(@PathVariable Long id) {
        try {
            Album album = albumService.obtenerAlbumPorId(id);
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> crearAlbum(@Valid @RequestBody CrearAlbumDTO dto) {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext()
                                        .getAuthentication()
                                        .getPrincipal();
        Album albumGuardado = albumService.crearAlbum(dto, securityUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(albumGuardado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAlbum(@PathVariable Long id) {
        albumService.eliminarAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> actualizarAlbum(@PathVariable Long id, @Valid @RequestBody CrearAlbumDTO dto) {
        try {
            Album albumActualizado = albumService.actualizarAlbum(id, dto);
            return ResponseEntity.ok(albumActualizado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
