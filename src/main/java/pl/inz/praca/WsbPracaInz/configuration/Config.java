package pl.inz.praca.WsbPracaInz.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("config")
public class Config {

    @Getter
    @Setter
    private String secretKey;

}