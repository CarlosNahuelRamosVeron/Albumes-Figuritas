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
    private String creador; //admin --> a cambiar por usuario
    @Enumerated(EnumType.STRING)
    private Dificultad dificultad; //facil, medio, dificil - calculada al publicar
    private Integer totalFiguritas = 10; //minimo 10
    private boolean publicado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contenido> contenidos = new ArrayList<>();
    
    public void addContenido(Contenido contenido) {
        this.contenidos.add(contenido);
        contenido.setAlbum(this);
        this.totalFiguritas = this.contenidos.stream().map(Contenido::contarFiguritas).reduce(0, Integer::sum);
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
