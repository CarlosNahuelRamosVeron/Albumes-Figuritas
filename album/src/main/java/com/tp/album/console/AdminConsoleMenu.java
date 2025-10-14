package com.tp.album.console;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.service.impl.AlbumService;

import java.util.List;
import java.util.Scanner;

public class AdminConsoleMenu {
    private final AlbumService albumService;
    private final Scanner scanner = new Scanner(System.in);

    public AdminConsoleMenu(AlbumService albumService) {
        this.albumService = albumService;
    }

    
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n=== Menú de Admin ===");
            System.out.println("1. Cargar datos masivos");
            System.out.println("2. Ver álbumes cargados");
            System.out.println("0. Salir");
            System.out.print("Ingrese opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> cargarDatosMasivos();
                case 2 -> verAlbumesCargados();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private void cargarDatosMasivos() {
        System.out.print("Ingrese ID del álbum a cargar: ");
        Long albumId = Long.parseLong(scanner.nextLine());

        System.out.println("Seleccione estrategia:");
        System.out.println("1. Uniforme");
        System.out.println("2. Ponderado");
        System.out.println("3. Automático (el sistema decide)");
        System.out.print("Opción: ");
        String modo;
        switch (scanner.nextLine()) {
            case "1" -> modo = "uniforme";
            case "2" -> modo = "ponderado";
            case "3" -> modo = "automatico";
            default -> {
                System.out.println("Opción inválida, usando automático por defecto.");
                modo = "automatico";
            }
        }

        // Simular DTOs de figuritas (por ejemplo 10 figuritas por álbum)
        List<CargarFiguritaDTO> figuritasDTO = albumService.generarFiguritasDTO(10);

        albumService.cargarFiguritas(albumId, figuritasDTO, modo);

        System.out.println("Figuritas cargadas con estrategia: " + modo);
    }

    private void verAlbumesCargados() {
        List<Album> albumes = albumService.obtenerAlbumes();
        if (albumes.isEmpty()) {
            System.out.println("No hay álbumes cargados.");
        } else {
            albumes.forEach(album -> System.out.println(
                "ID: " + album.getId() +
                " | Título: " + album.getTitulo() +
                " | Figus cargadas: " + album.getFiguritas().size() +
                " | Publicado: " + album.isPublicado()
            ));
        }
    }
}
