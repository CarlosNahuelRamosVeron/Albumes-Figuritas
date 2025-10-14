package com.tp.album.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String dificultad; //facil, medio, dificil - calculada al publicar
    private Integer totalFiguritas = 10; //minimo 10
    private boolean publicado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @OneToMany(
        mappedBy = "album",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Figurita> figuritas = new ArrayList<>();
    
    public void addFigurita(Figurita figurita) {
        figuritas.add(figurita);
        figurita.setAlbum(this);
        this.totalFiguritas = figuritas.size();
    }
}
