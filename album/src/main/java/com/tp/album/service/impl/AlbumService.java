package com.tp.album.service.impl;

import com.tp.album.entities.Album;
import com.tp.album.entities.Rareza;
import com.tp.album.entities.Figurita;
import com.tp.album.dto.CargarFiguritaDTO;
import com.tp.album.repository.AlbumRepository;
import com.tp.album.repository.FiguritaRepository;
import com.tp.album.service.DistributionStrategy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final FiguritaRepository figuritaRepository;
    private final FiguritaService figuritaService;
    private final ImagenService imagenService;

    public AlbumService(AlbumRepository albumRepository,
                        FiguritaRepository figuritaRepository,
                        FiguritaService figuritaService,
                        ImagenService imagenService) {
        this.albumRepository = albumRepository;
        this.figuritaRepository = figuritaRepository;
        this.figuritaService = figuritaService;
        this.imagenService = imagenService;
    }

    public Album crearAlbum(Album album) {
        album.setPublicado(false);
        return albumRepository.save(album);
    }

    @Transactional
    public List<Figurita> cargarFiguritas(Long albumId,
                                        List<CargarFiguritaDTO> metadata,
                                        List<MultipartFile> imagenes,
                                        DistributionStrategy strategy) throws Exception {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album no encontrado"));

        if (imagenes.size() != metadata.size()) {
            throw new IllegalArgumentException("Metadata e imágenes deben coincidir");
        }            

        // Guardar imagenes y actualizar la url
        for (int i = 0; i < imagenes.size(); i++) {
            String url = imagenService.guardarYValidarImagenDeAlbum(albumId, imagenes.get(i), Paths.get("cargas"));
            metadata.get(i).setUrl(url);
        }

        // Crear figuritas
        List<Figurita> figuritas = figuritaService.crearFiguritas(album, metadata, strategy, 100);

        // Guardar figuritas 
        for (Figurita figurita : figuritas) {
            figuritaRepository.save(figurita);
            album.addFigurita(figurita);
        }

        // Guardar album
        albumRepository.save(album);

        return figuritas;
    }

    @Transactional
    public Album publicarAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow();
        // calcular dificultad por rareza promedio
        double score = album.getFiguritas().stream()
            .mapToDouble(s -> 
                s.getRareza() == Rareza.COMUN ? 1 
                    : s.getRareza() == Rareza.RARA ? 2 
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
}