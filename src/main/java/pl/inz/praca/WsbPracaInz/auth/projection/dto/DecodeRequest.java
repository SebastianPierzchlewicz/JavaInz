package pl.inz.praca.WsbPracaInz.auth.projection.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public
class DecodeRequest
{
    @NotBlank(message = "Musisz podać token")
    private String token;
}
