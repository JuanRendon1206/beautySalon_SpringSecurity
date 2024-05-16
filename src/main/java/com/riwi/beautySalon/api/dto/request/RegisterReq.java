package com.riwi.beautySalon.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReq {
    
    @NotBlank(message = "El nombre de el usuario es obligatorio")
    @Size(min = 8, max = 150, message = "El usuario debe de tener entre 8 y 150 caracteres")
    private String userName;

    @NotBlank(message = "La contraseña de el usuario es obligatorio")
    @Size(min = 8, max = 150, message = "La contraseña debe de tener entre 8 y 150 caracteres")
    //@Pattern(regexp = "expresion regular")
    private String password;
}
