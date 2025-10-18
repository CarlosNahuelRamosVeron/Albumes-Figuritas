package com.tp.album.service.impl;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.dto.CrearAlbumDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.repository.AlbumRepository;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.strategy.DistributionStrategyFactory;
import com.tp.album.service.strategy.ModoDistribucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ContenidoService contenidoService;
    private final DistributionStrategyFactory strategyFactory;

    public AlbumService(AlbumRepository albumRepository,
                        ContenidoService contenidoService,
                        DistributionStrategyFactory strategyFactory) {
        this.albumRepository = albumRepository;
        this.contenidoService = contenidoService;
        this.strategyFactory = strategyFactory;
    }

    public Album crearAlbum(CrearAlbumDTO dto) {
        Album album = new Album();
        album.setTitulo(dto.getTitulo());
        album.setDescripcion(dto.getDescripcion());
        album.setCategoria(dto.getCategoria());
        album.setPublicado(false);
        return albumRepository.save(album);
    }

    public List<Album> obtenerAlbumes() {
        return albumRepository.findAll();
    }

    public Album obtenerAlbumPorId(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));
    }

    @Transactional
    public void eliminarAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    public List<Contenido> obtenerContenido(Long albumId) {
        Album album = this.obtenerAlbumPorId(albumId);
        return album.getContenidos();
    }

    @Transactional
    public List<Contenido> cargarContenido(Long albumId, List<ContenidoDTO> contenidosDTO, ModoDistribucion modo) {
        Album album = this.obtenerAlbumPorId(albumId);
        DistributionStrategy strategy = strategyFactory.elegirEstrategiaSegunAlbum(album, modo);
        return contenidoService.creaContenidos(album, contenidosDTO, strategy, 10);
    }

    @Transactional
    public Album publicarAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow();
        Double rarezaScore = album.calcularRarezaPromedio();
        album.setDificultadByRarezaScore(rarezaScore);
        album.setPublicado(true);
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
