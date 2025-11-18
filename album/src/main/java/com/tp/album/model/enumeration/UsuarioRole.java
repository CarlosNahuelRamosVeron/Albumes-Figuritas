package com.tp.album.model.enumeration;

public enum UsuarioRole {
    ADMIN("ADMIN"),
    USUARIO("USUARIO");

    private final String valor;

    UsuarioRole(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
