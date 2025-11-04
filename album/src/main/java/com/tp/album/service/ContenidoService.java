package com.tp.album.service;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.repository.ContenidoRepository;
import org.springframework.stereotype.Service;

import com.tp.album.model.entities.Album;
import com.tp.album.model.enumeration.ModoDistribucion;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.strategy.DistributionStrategyFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    private final Integer DEFAULT_STOCK_POR_CONTENIDO = 10;

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

    @Transactional(readOnly = true)
    public Contenido obtenerContenido(Long contenidoId) {
        return this.contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new NoSuchElementException("Contenido no encontrado"));
    }

    @Transactional
    public List<Contenido> cargarContenido(Long albumId, List<ContenidoDTO> contenidosDTO, ModoDistribucion modo) {
        Album album = this.albumService.obtenerAlbumPorId(albumId);
        DistributionStrategy strategy = strategyFactory.elegirEstrategiaSegunAlbum(album, modo);
        List<Contenido> creados = this.creaContenidos(album, contenidosDTO, strategy, DEFAULT_STOCK_POR_CONTENIDO);
        this.contenidoRepository.saveAll(creados);
        return creados;
    }

    private List<Contenido> creaContenidos(Album album, List<ContenidoDTO> contenidosDTO, DistributionStrategy strategy, int defaultStock) {
        return contenidosDTO.stream()
                .map(dto -> this.creadorContenidoFactory.create(strategy, defaultStock, album, dto))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarContenido(Long contenidoId) {
        this.contenidoRepository.deleteById(contenidoId);
    }

}