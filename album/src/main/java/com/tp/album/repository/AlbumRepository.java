package com.tp.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.album.entities.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
