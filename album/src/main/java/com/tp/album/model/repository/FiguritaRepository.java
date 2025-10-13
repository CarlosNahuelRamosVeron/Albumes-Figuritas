package com.tp.album.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tp.album.model.entities.Figurita;

public interface FiguritaRepository extends JpaRepository<Figurita, Long> {

    // Decremento atómico: update solo si stockAvailable >= amount (evita negativos)
    @Modifying
    @Query("UPDATE Figurita f "
        + "SET f.stockDisponible = f.stockDisponible - ?2 "
        + "WHERE f.id = ?1 "
        + "AND f.stockDisponible >= ?2")
    int restarStockSiEstaDisponible(Long stickerId, int amount);
}