package com.tp.album.service.strategy.impl;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Figurita;
import com.tp.album.service.strategy.CreadorContenidoStrategy;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.validation.ImageValidation;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class CreadorFiguritaStrategy extends CreadorContenidoStrategy {

    public CreadorFiguritaStrategy(DistributionStrategy strategy, int defaultStock, ImageValidation imageValidation) {
        super(strategy, defaultStock, imageValidation);
    }

    @Override
    public Figurita crearDesdeDTO(ContenidoDTO dto, Album album) {
        CargarFiguritaDTO figuritaDTO = (CargarFiguritaDTO) dto;

        MultipartFile archivo = figuritaDTO.getArchivoImagen();
        this.getImageValidation().validar(archivo);

        Figurita figurita = new Figurita();
        figurita.setNombre(figuritaDTO.getNombre());
        figurita.setNumero(figuritaDTO.getNumero());
        figurita.setAlbum(album);

        String url = guardarImagenLocal(archivo);
        figurita.setUrlImagen(url);

        this.getStrategy().asignarRarezaYStock(figurita, this.getDefaultStock());
        return figurita;
    }
    private String guardarImagenLocal(MultipartFile archivo) {
        try {
            String carpetaDestino = "uploads/";
            File directorio = new File(carpetaDestino);
            if (!directorio.exists()) directorio.mkdirs();

            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get(carpetaDestino, nombreArchivo);
            Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen: " + e.getMessage(), e);
        }
    }
}
