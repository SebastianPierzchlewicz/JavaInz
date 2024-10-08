package pl.inz.praca.WsbPracaInz.auth.projection.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public
class FixPasswordRequest
{
    @NotBlank(message = "Musisz podać nazwę użytkownika")
    private String username;
    @NotBlank(message = "Musisz podać hasło")
    private String password;
    @NotBlank(message = "Musisz podać token")
    private String token;
}
