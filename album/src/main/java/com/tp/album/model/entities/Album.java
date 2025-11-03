package com.tp.album.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tp.album.model.enumeration.Dificultad;

@Data
@Entity
@Table(name = "album")
public class Album {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String creador;
    @Enumerated(EnumType.STRING)
    private Dificultad dificultad;
    private transient Integer totalFiguritas;
    private boolean publicado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contenido> contenidos = new ArrayList<>();
    
    public void addContenido(Contenido contenido) {
        this.contenidos.add(contenido);
        contenido.setAlbum(this);
    }

    public Integer getTotalFiguritas() {
        return this.calcularTotalFiguritas();
    }

    private Integer calcularTotalFiguritas() {
        return this.contenidos.stream()
                .filter(Contenido::isRoot)
                .map(Contenido::contarFiguritas)
                .reduce(0, Integer::sum);
    }

    public Double calcularRarezaPromedio() {
        return this.getContenidos().stream()
                .mapToDouble(Contenido::getRarezaValue)
                .average().orElse(1.0);
    }

    public void setDificultadByRarezaScore(double rarezaScore) {
        this.dificultad = Dificultad.fromScore(rarezaScore);
    }
}
