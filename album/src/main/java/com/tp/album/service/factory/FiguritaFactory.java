package com.tp.album.service.factory;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Figurita;
import com.tp.album.service.strategy.DistributionStrategy;

public class FiguritaFactory extends ContenidoFactory {

    public FiguritaFactory(DistributionStrategy strategy, int defaultStock) {
        super(strategy, defaultStock);
    }

    @Override
    public Figurita crearDesdeDTO(ContenidoDTO dto, Album album) {
        CargarFiguritaDTO figuritaDTO = (CargarFiguritaDTO) dto;
        Figurita figurita = new Figurita();
        figurita.setNombre(figuritaDTO.getNombre());
        figurita.setNumero(figuritaDTO.getNumero());
        figurita.setUrlImagen(figuritaDTO.getUrl());
        figurita.setAlbum(album);
        this.getStrategy().asignarRarezaYStock(figurita, this.getDefaultStock());
        return figurita;
    }
}
