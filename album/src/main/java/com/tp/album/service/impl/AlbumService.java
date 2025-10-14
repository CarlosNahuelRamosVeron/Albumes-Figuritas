package com.tp.album.service.impl;

import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Rareza;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.repository.AlbumRepository;
import com.tp.album.model.repository.FiguritaRepository;
import com.tp.album.service.DistributionStrategy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final FiguritaRepository figuritaRepository;
    private final FiguritaService figuritaService;

    public AlbumService(AlbumRepository albumRepository,
                        FiguritaRepository figuritaRepository,
                        FiguritaService figuritaService) {
        this.albumRepository = albumRepository;
        this.figuritaRepository = figuritaRepository;
        this.figuritaService = figuritaService;
    }

    public Album crearAlbum(CrearAlbumDTO dto) {
        Album album = new Album();
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        album.setPublicado(false);
        return albumRepository.save(album);
    }

    public List<Album> obtenerAlbums() {
        return albumRepository.findAll();
    }

    public Album obtenerAlbumPorId(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));
    }

    @Transactional
    public void eliminarAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));
        albumRepository.delete(album);
    }

    public List<Figurita> obtenerFiguritas(Long albumId) {
        return albumRepository.findById(albumId).get().getFiguritas();
    }

    @Transactional
    public List<Figurita> cargarFiguritas(Long albumId,
                                        List<CargarFiguritaDTO> cargarFiguritaDTOs,
                                        DistributionStrategy strategy) {
        Album album = obtenerAlbumPorId(albumId);
        List<Figurita> figuritas = figuritaService.crearFiguritas(album, cargarFiguritaDTOs, strategy, 10);
        for (Figurita figurita : figuritas) {
            figuritaRepository.save(figurita);
            album.addFigurita(figurita);
        }
        albumRepository.save(album);
        return figuritas;
    }

    @Transactional
    public Album publicarAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow();
        // calcular dificultad por rareza promedio
        double score = album.getFiguritas().stream()
            .mapToDouble(f -> 
                f.getRareza() == Rareza.COMUN ? 1 
                    : f.getRareza() == Rareza.RARA ? 2 
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

    @Transactional
    public Album actualizarAlbum(Long id, CrearAlbumDTO dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        return albumRepository.save(album);
    }
}