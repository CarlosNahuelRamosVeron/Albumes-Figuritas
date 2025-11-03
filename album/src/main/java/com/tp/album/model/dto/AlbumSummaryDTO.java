package com.tp.album.model.dto;

import com.tp.album.model.entities.Album;
import com.tp.album.model.enumeration.Dificultad;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumSummaryDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String creador;
    private Dificultad dificultad;
    private Integer totalFiguritas;
    private boolean publicado;
    private LocalDateTime fechaCreacion;

    public AlbumSummaryDTO() {
    }

    public AlbumSummaryDTO(Album album) {
        this.id = album.getId();
        this.titulo = album.getTitulo();
        this.descripcion = album.getDescripcion();
        this.categoria = album.getCategoria();
        this.creador = album.getCreador();
        this.dificultad = album.getDificultad();
        this.totalFiguritas = album.getTotalFiguritas();
        this.publicado = album.isPublicado();
        this.fechaCreacion = album.getFechaCreacion();
    }


}

