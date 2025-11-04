package com.tp.album.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenResponseDTO {
    private String token;
    private String tokenType = "Bearer";

    public TokenResponseDTO(String token) {
        this.token = token;
    }

}

