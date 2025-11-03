package com.tp.album.service.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class ImageValidation {
    private static final long MAX_SIZE = 5_000_000;
    private static final int MAX_WIDTH = 800; //segun puso el profe como ejemplo en "Reglas de Negocio"
    private static final int MAX_HEIGHT = 600;

    public static void validar(byte[] imagenBytes) {
        if (imagenBytes == null || imagenBytes.length == 0) {
            throw new IllegalArgumentException("La imagen es obligatoria");
        }
        if (imagenBytes.length > MAX_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (5MB)");
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imagenBytes)) {
            BufferedImage imagen = ImageIO.read(bais);
            if (imagen == null) {
                throw new IllegalArgumentException("El contenido provisto no es una imagen válida");
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