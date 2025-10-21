package com.tp.album.service.factory;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Figurita;
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

public class FiguritaFactory extends ContenidoFactory {

    public FiguritaFactory(DistributionStrategy strategy, int defaultStock) {
        super(strategy, defaultStock);
    }

    @Override
    public Figurita crearDesdeDTO(ContenidoDTO dto, Album album, ImageValidation validator) {
        CargarFiguritaDTO figuritaDTO = (CargarFiguritaDTO) dto;

        MultipartFile archivo = figuritaDTO.getArchivoImagen();
        validator.validar(archivo);

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
