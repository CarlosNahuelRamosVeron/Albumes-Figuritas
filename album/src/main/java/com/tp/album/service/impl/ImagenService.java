package com.tp.album.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class ImagenService {

    private static final long MAX_BYTES = 2 * 1024 * 1024; // 2 MB
    private static final int REQUIRED_WIDTH = 600;
    private static final int REQUIRED_HEIGHT = 800;

    public String guardarYValidarImagenDeAlbum(Long albumId, MultipartFile archivo, Path ruta) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacio");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !(contentType.contains("jpeg") || contentType.contains("png"))) {
            throw new IllegalArgumentException("Formato no soportado (usar PNG/JPEG)");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Archivo demasiado grande");
        }
        BufferedImage img = ImageIO.read(archivo.getInputStream());
        if (img == null) {
            throw new IllegalArgumentException("No es una imagen valida");
        }
        if (img.getWidth() != REQUIRED_WIDTH || img.getHeight() != REQUIRED_HEIGHT) {
            throw new IllegalArgumentException("Dimensiones incorrectas: requeridas " + REQUIRED_WIDTH + "x" + REQUIRED_HEIGHT);
        }
        Path albumDir = ruta.resolve("albums").resolve(String.valueOf(albumId));
        albumDir.toFile().mkdirs();
        String filename = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
        Path dest = albumDir.resolve(filename);
        archivo.transferTo(dest.toFile());
        return "/uploads/albums/" + albumId + "/" + filename; //TODO url relativo - ajustar según hosting
    }
}

