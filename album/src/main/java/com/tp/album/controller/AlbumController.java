package com.tp.album.controller;

import com.tp.album.config.SecurityUser;
import com.tp.album.model.dto.AlbumResponseDTO;
import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.mapper.AlbumMapper;
import com.tp.album.model.mapper.ContenidoMapper;
import com.tp.album.service.AlbumService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDTO>> obtenerAlbumes() {
        List<Album> albums = albumService.obtenerAlbumes();
        List<AlbumResponseDTO> resp = albums.stream()
                .map(a -> albumMapper.toAlbumResponseDTO(a, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AlbumResponseDTO> obtenerAlbumPorId(@PathVariable Long id) {
        try {
            Album album = albumService.obtenerAlbumPorId(id);
            AlbumResponseDTO resp = albumMapper.toAlbumResponseDTO(album, true);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<AlbumResponseDTO> publicar(@PathVariable Long id) {
        Album albumGuardado = albumService.publicarAlbum(id);
        AlbumResponseDTO resp = albumMapper.toAlbumResponseDTO(albumGuardado, false);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDTO> crearAlbum(@Valid @RequestBody CrearAlbumDTO dto) {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext()
                                        .getAuthentication()
                                        .getPrincipal();
        Album albumGuardado = albumService.crearAlbum(dto, securityUser.getUsername());
        AlbumResponseDTO resp = albumMapper.toAlbumResponseDTO(albumGuardado, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAlbum(@PathVariable Long id) {
        albumService.eliminarAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDTO> actualizarAlbum(@PathVariable Long id, @Valid @RequestBody CrearAlbumDTO dto) {
        try {
            Album albumActualizado = albumService.actualizarAlbum(id, dto);
            AlbumResponseDTO resp = albumMapper.toAlbumResponseDTO(albumActualizado, false);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
