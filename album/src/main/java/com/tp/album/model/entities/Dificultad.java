package com.tp.album.model.entities;

public enum Dificultad {
    FACIL(0, 1.5),
    MEDIO(1.5, 2.5),
    DIFICIL(2.5, Double.MAX_VALUE);

    private final double minScore;
    private final double maxScore;

    Dificultad(double minScore, double maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static Dificultad fromScore(double score) {
        for (Dificultad dificultad : Dificultad.values()) {
            if (score >= dificultad.minScore && score < dificultad.maxScore) {
                return dificultad;
            }
        }
        throw new IllegalArgumentException("Score fuera de rango: " + score);
    }
}
