package com.tp.album.model.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("SECCION")
public class Seccion extends Contenido {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contenido> contenidos = new ArrayList<>();

    public void addContenido(Contenido c) {
        contenidos.add(c);
        c.setParent(this);
    }
    public void removeContenido(Contenido c) {
        contenidos.remove(c);
        c.setParent(null);
    }

    @Override
    public Integer contarFiguritas() {
        return contenidos.stream()
                .mapToInt(Contenido::contarFiguritas)
                .sum();
    }

    @Override
    public double getRarezaValue() {
        int totalFiguritas = 0;
        double sumaPonderada = 0.0;

        for (Contenido c : contenidos) {
            Integer cntObj = c.contarFiguritas();
            int cnt = (cntObj == null) ? 0 : cntObj;
            if (cnt > 0) {
                sumaPonderada += c.getRarezaValue() * cnt;
                totalFiguritas += cnt;
            }
        }

        return totalFiguritas == 0 ? 1.0 : sumaPonderada / totalFiguritas;
    }
}
