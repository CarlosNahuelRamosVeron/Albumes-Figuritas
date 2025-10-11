package com.tp.album.service.impl;

import org.springframework.stereotype.Service;

import com.tp.album.dto.CargarFiguritaDTO;
import com.tp.album.entities.Album;
import com.tp.album.entities.Figurita;
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
        //int num = 1;
        for (CargarFiguritaDTO dto : cargarFiguritaDTOs) {
            Figurita s = new Figurita();
            s.setNombre(dto.getNombre());
            s.setNumero(dto.getNumero());
            s.setAlbum(album);
            s.setUrl(dto.getUrl()); // imageUrl set by ImageService
            figuritas.add(s);
        }
        strategy.asignarRarezaYStock(figuritas, defaultStock);
        return figuritas;
    }
    
}
