package com.tp.album.service;

import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album crearAlbum(CrearAlbumDTO dto, String creador) {
        Album album = new Album();
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        album.setCreador(creador);
        return albumRepository.save(album);
    }

    public List<Album> obtenerAlbumes() {
        return albumRepository.findAll();
    }

    public Album obtenerAlbumPorId(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new NoSuchElementException("Album no encontrado"));
    }

    @Transactional
    public void eliminarAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    @Transactional
    public Album publicarAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow();
        Double rarezaScore = album.calcularRarezaPromedio();
        album.setDificultadByRarezaScore(rarezaScore);
        album.publicar();
        return albumRepository.save(album);
    }

    @Transactional
    public Album actualizarAlbum(Long id, CrearAlbumDTO dto) {
        Album album = this.obtenerAlbumPorId(id);
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        return albumRepository.save(album);
    }

}
