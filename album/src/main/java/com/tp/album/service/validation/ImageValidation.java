package com.tp.album.service.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
public class ImageValidation {
    private static final long MAX_SIZE = 5_000_000;
    private static final int MAX_WIDTH = 800; //segun puso el profe como ejemplo en "Reglas de Negocio"
    private static final int MAX_HEIGHT = 600;

    public void validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }
        if (!archivo.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Formato no permitido, debe ser una imagen");
        }
        if (archivo.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (5MB)");
        }
        try {
            BufferedImage imagen = ImageIO.read(archivo.getInputStream());
            if (imagen == null) {
                throw new IllegalArgumentException("El archivo no contiene una imagen válida");
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            if (ancho > MAX_WIDTH || alto > MAX_HEIGHT) {
                throw new IllegalArgumentException("La imagen excede las dimensiones permitidas (" +
                        MAX_WIDTH + "x" + MAX_HEIGHT + ")");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen: " + e.getMessage(), e);
        }
    }
}