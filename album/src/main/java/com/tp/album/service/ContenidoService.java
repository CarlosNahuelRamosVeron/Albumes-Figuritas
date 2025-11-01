package com.tp.album.service;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.repository.ContenidoRepository;
import com.tp.album.service.strategy.CreadorContenidoFactory;
import org.springframework.stereotype.Service;

import com.tp.album.model.entities.Album;
import com.tp.album.model.enumeration.ModoDistribucion;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.strategy.DistributionStrategyFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    private final CreadorContenidoFactory creadorContenidoFactory;
    private final AlbumService albumService;
    private final DistributionStrategyFactory strategyFactory;
    private final ContenidoRepository contenidoRepository;

    public ContenidoService(CreadorContenidoFactory creadorContenidoFactory,
                            AlbumService albumService,
                            DistributionStrategyFactory strategyFactory,
                            ContenidoRepository contenidoRepository) {
        this.creadorContenidoFactory = creadorContenidoFactory;
        this.albumService = albumService;
        this.strategyFactory = strategyFactory;
        this.contenidoRepository = contenidoRepository;
    }

    public Contenido obtenerContenido(Long contenidoId) {
        return this.contenidoRepository.findById(contenidoId).orElseThrow(() -> new IllegalArgumentException("Contenido no encontrado"));
    }

    public List<Contenido> obtenerContenidoByAlbumId(Long albumId) {
        Album album = albumService.obtenerAlbumPorId(albumId);
        return album.getContenidos();
    }

    @Transactional
    public List<Contenido> cargarContenido(Long albumId, List<ContenidoDTO> contenidosDTO, ModoDistribucion modo) {
        Album album = this.albumService.obtenerAlbumPorId(albumId);
        DistributionStrategy strategy = strategyFactory.elegirEstrategiaSegunAlbum(album, modo);
        return this.creaContenidos(album, contenidosDTO, strategy, 10);
    }

    private List<Contenido> creaContenidos(Album album, List<ContenidoDTO> contenidosDTO, DistributionStrategy strategy, int defaultStock) {
        return contenidosDTO.stream()
                .map(dto -> this.creadorContenidoFactory.create(strategy, defaultStock, album, dto))
                .collect(Collectors.toList());
    }

    public void eliminarContenido(Long contenidoId) {
        this.contenidoRepository.deleteById(contenidoId);
    }

}