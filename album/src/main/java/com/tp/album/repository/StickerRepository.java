package com.tp.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tp.album.entities.Sticker;

public interface StickerRepository extends JpaRepository<Sticker, Long> {

    // Decremento atómico: update solo si stockAvailable >= amount (evita negativos)
    @Modifying
    @Query("UPDATE Sticker s "
        + "SET s.stockDisponible = s.stockDisponible - ?2 "
        + "WHERE s.id = ?1 "
        + "AND s.stockDisponible >= ?2")
    int decrementStockIfAvailable(Long stickerId, int amount);
}