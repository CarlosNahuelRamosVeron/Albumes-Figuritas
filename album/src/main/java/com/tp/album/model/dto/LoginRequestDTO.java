package com.tp.album.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

}

