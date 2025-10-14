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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final FiguritaRepository figuritaRepository;
    private final FiguritaService figuritaService;
    private final Map<String, DistributionStrategy> estrategias;

    public AlbumService(AlbumRepository albumRepository,
                        FiguritaRepository figuritaRepository,
                        FiguritaService figuritaService,
                        Map<String, DistributionStrategy> estrategias) {
        this.albumRepository = albumRepository;
        this.figuritaRepository = figuritaRepository;
        this.figuritaService = figuritaService;
        this.estrategias = estrategias;
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
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));
        albumRepository.delete(album);
    }

    public List<Figurita> obtenerFiguritas(Long albumId) {
        return albumRepository.findById(albumId).get().getFiguritas();
    }

    @Transactional
    public void cargarFiguritas(Long albumId,
                                List<CargarFiguritaDTO> figuritasDTO,
                                String modo) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Álbum no encontrado"));

        DistributionStrategy strategy;

        // 🔹 Si el admin pide automático, el sistema decide
        if (modo.equalsIgnoreCase("automatico")) {
            strategy = elegirEstrategiaSegunAlbum(album);
        } else {
            // 🔹 Si elige explícitamente, se usa la que corresponda
            strategy = estrategias.getOrDefault(modo, estrategias.get("uniforme"));
        }

        figuritaService.crearFiguritas(album, figuritasDTO, strategy, 10);
    }

    private DistributionStrategy elegirEstrategiaSegunAlbum(Album album) {
        int cantidad = album.getFiguritas().size();
        if (cantidad < 10) {
            return estrategias.get("uniforme");
        } else {
            return estrategias.get("ponderado");
        }
    }

    public List<CargarFiguritaDTO> generarFiguritasDTO(int cantidad) {
        List<CargarFiguritaDTO> lista = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            CargarFiguritaDTO dto = new CargarFiguritaDTO();
            dto.setNombre("Figurita " + i);
            dto.setNumero(i);
            lista.add(dto);
        }
        return lista;
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