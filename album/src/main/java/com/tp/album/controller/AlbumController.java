package com.tp.album.controller;

import com.tp.album.dto.CrearAlbumDTO;
import com.tp.album.entities.Album;
import com.tp.album.service.impl.AlbumService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    public ResponseEntity<Album> crearAlbum(@RequestBody CrearAlbumDTO dto) {
        Album albumGuardado = albumService.crearAlbum(dto);
        return ResponseEntity.ok(albumGuardado);
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<Album> publicar(@PathVariable Long id) {
        Album albumGuardado = albumService.publicarAlbum(id);
        return ResponseEntity.ok(albumGuardado);
    }

    @GetMapping
    public ResponseEntity<List<Album>> obetenerAlbumes() {
        return ResponseEntity.ok(albumService.obetenerAlbumes());
    }

}
