package com.tp.album.service.impl;

import org.springframework.stereotype.Service;

import com.tp.album.model.dto.CargarFiguritaDTO;
import com.tp.album.model.entities.Album;
import com.tp.album.model.entities.Figurita;
import com.tp.album.service.DistributionStrategy;

import java.util.ArrayList;
import java.util.List;

@Service
public class FiguritaService {

    public List<Figurita> crearFiguritas(Album album,
                                    List<CargarFiguritaDTO> cargarFiguritaDTOs,
                                    DistributionStrategy strategy,
                                    int defaultStock) {
        List<Figurita> figuritas = new ArrayList<>();
        for (CargarFiguritaDTO dto : cargarFiguritaDTOs) {
            Figurita figurita = new Figurita();
            figurita.setNombre(dto.getNombre());
            figurita.setNumero(dto.getNumero());
            figurita.setAlbum(album);
            figuritas.add(figurita);
        }
        strategy.asignarRarezaYStock(figuritas, defaultStock);
        return figuritas;
    }
    
}
