package com.tp.album.entities;

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
    private String dificultad; //facil, medio, dificil - calculada al publicar
    private Integer totalStickers = 0;
    private boolean publicado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @OneToMany(
        mappedBy = "album",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Sticker> stickers = new ArrayList<>();
    
    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
        sticker.setAlbum(this);
        this.totalStickers = stickers.size();
    }
}
