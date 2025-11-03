package com.tp.album.service;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.CargarSeccionDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.entities.Figurita;
import com.tp.album.model.entities.Seccion;
import com.tp.album.service.strategy.DistributionStrategy;
import com.tp.album.service.validation.ImageValidation;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
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

        Figurita figurita = new Figurita();
        figurita.setNombre(figuritaDTO.getNombre());
        figurita.setNumero(figuritaDTO.getNumero());
        album.addContenido(figurita);

        String b64 = figuritaDTO.getImagenBase64();
        if (b64 != null && !b64.isBlank()) {
            byte[] bytes = decodeBase64Image(b64);
            ImageValidation.validar(bytes);
            String url = guardarImagenLocal(bytes, guessExtensionFromBase64Header(b64));
            figurita.setUrlImagen(url);
        }

        strategy.asignarRarezaYStock(figurita, defaultStock);
        return figurita;
    }

    private String guardarImagenLocal(byte[] bytes, String extension) {
        try {
            String carpetaDestino = "uploads/";
            File directorio = new File(carpetaDestino);
            if (!directorio.exists()) directorio.mkdirs();

            String ext = (extension != null && !extension.isBlank()) ? extension : "png";
            String nombreArchivo = UUID.randomUUID() + "." + ext;
            Path ruta = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(ruta, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return "/uploads/" + nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen: " + e.getMessage(), e);
        }
    }

    private byte[] decodeBase64Image(String base64) {
        String data = base64;
        int comma = base64.indexOf(",");
        if (base64.startsWith("data:") && comma > 0) {
            data = base64.substring(comma + 1);
        }
        return Base64.getDecoder().decode(data);
    }

    private String guessExtensionFromBase64Header(String base64) {
        if (base64 == null) return null;
        if (base64.startsWith("data:image/")) {
            String withoutPrefix = base64.substring("data:image/".length());
            int end = withoutPrefix.indexOf(";");
            if (end < 0) {
                end = withoutPrefix.indexOf(",");
            }
            if (end > 0) {
                String token = withoutPrefix.substring(0, end).toLowerCase();
                if ("jpeg".equals(token)) return "jpg";
                if ("jpg".equals(token) || "png".equals(token) || "gif".equals(token) || "webp".equals(token)) {
                    return token;
                }
            }
        }
        return null;
    }

    private Contenido crearSeccion(DistributionStrategy strategy, int defaultStock, Album album, ContenidoDTO dto) {
        CargarSeccionDTO seccionDTO = (CargarSeccionDTO) dto;
        Seccion seccion = new Seccion();
        seccion.setNombre(seccionDTO.getNombre());
        album.addContenido(seccion);

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