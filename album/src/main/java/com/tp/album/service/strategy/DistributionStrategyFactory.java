package com.tp.album.service.strategy;

import com.tp.album.model.entities.Album;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DistributionStrategyFactory {
    private final Map<String, DistributionStrategy> estrategias;

    public DistributionStrategyFactory(Map<String, DistributionStrategy> estrategias) {
        this.estrategias = estrategias;
    }

    public DistributionStrategy elegirEstrategiaSegunAlbum(Album album, String modo) {
        if (modo.equalsIgnoreCase("automatico")) {
            return album.getFiguritas().size() < 10 ?
                    estrategias.get("uniforme") : estrategias.get("ponderado");
        }
        return estrategias.getOrDefault(modo, estrategias.get("uniforme"));
    }
}
