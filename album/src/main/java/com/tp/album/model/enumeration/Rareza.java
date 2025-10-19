package com.tp.album.model.enumeration;

public enum Rareza {
    COMUN(1),
    RARA(2),
    EPICA(3);

    private final int valor;

    Rareza(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

}
