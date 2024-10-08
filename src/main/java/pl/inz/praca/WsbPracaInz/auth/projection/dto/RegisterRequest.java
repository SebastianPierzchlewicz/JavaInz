package pl.inz.praca.WsbPracaInz.auth.projection.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest
{
    @NotBlank(message = "Musisz podać email")
    private @Email String email;
    @NotBlank(message = "Musisz podać nazwę użytkownika")
    private String username;
    @NotBlank(message = "Musisz podać hasło")
    private String password;
    @NotBlank(message = "Musisz powtórzyć hasło")
    private String password_confirmed;
}
