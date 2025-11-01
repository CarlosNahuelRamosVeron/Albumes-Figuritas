package com.tp.album.service.strategy;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.CargarSeccionDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.entities.Seccion;
import com.tp.album.service.validation.ImageValidation;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public  class CreadorContenidoFactory {

    public Contenido create(DistributionStrategy strategy, int defaultStock, Album album, ContenidoDTO dto) {
        switch (dto.getTipo()) {
            case "FIGURITA": return this.crearFigurita(strategy, defaultStock, album, dto);
            case "SECCION": return this.crearSeccion(strategy, defaultStock, album, dto);
            default: throw new IllegalArgumentException("Tipo de contenido no soportado: " + dto.getTipo());
        }
    }

    private  Contenido crearFigurita(DistributionStrategy strategy, int defaultStock, Album album, ContenidoDTO dto) {
        CargarFiguritaDTO figuritaDTO = (CargarFiguritaDTO) dto;

        MultipartFile archivo = figuritaDTO.getArchivoImagen();
        ImageValidation.validar(archivo);

        Figurita figurita = new Figurita();
        figurita.setNombre(figuritaDTO.getNombre());
        figurita.setNumero(figuritaDTO.getNumero());
        figurita.setAlbum(album);

        String url = guardarImagenLocal(archivo);
        figurita.setUrlImagen(url);

        strategy.asignarRarezaYStock(figurita, defaultStock);
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

    private Contenido crearSeccion(DistributionStrategy strategy, int defaultStock, Album album, ContenidoDTO dto) {
        CargarSeccionDTO seccionDTO = (CargarSeccionDTO) dto;
        Seccion seccion = new Seccion();
        seccion.setNombre(seccionDTO.getNombre());
        seccion.setAlbum(album);

        if (seccionDTO.getContenidos() != null && !seccionDTO.getContenidos().isEmpty()) {
            agregarContenido(strategy, defaultStock, seccionDTO, seccion);
        }
        return seccion;
    }
    private void agregarContenido(DistributionStrategy strategy, int defaultStock, CargarSeccionDTO seccionDTO, Seccion seccion) {
        List<Contenido> contenidosHijos = seccionDTO.getContenidos().stream()
                .map(contenidoDTO -> this.create(strategy, defaultStock, seccion.getAlbum(), contenidoDTO))
                .collect(Collectors.toList());
        contenidosHijos.forEach(contenido -> contenido.setParent(seccion));
        seccion.setContenidos(contenidosHijos);
    }

}