package com.tp.album.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CargarFiguritaDTO.class, name = "FIGURITA"),
        @JsonSubTypes.Type(value = CargarSeccionDTO.class, name = "SECCION")
})
public interface ContenidoDTO {
    String getTipo();
}
