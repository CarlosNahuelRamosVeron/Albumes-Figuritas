package com.tp.album.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.album.model.entities.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
