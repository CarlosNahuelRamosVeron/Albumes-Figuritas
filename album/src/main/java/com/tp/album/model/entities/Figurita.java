package com.tp.album.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("FIGURITA")
public class Figurita extends Contenido {

    private int numero;
    @Enumerated(EnumType.STRING)
    private Rareza rareza;
    private Integer stockTotal = 0;
    private Integer stockDisponible = 0;
    private String urlImagen;


    @Override
    public Integer contarFiguritas() {
        return 1;
    }

    @Override
    public double getRarezaValue() {
        return this.rareza.getValor();
    }

}
