package pl.inz.praca.WsbPracaInz.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class BodySizeRequest
{
    @Nullable
    private Long id;
    @Nullable
    private Double chest;
    @Nullable
    private Double neck;
    @Nullable
    private Double biceps;
    @Nullable
    private Double waist;
    @Nullable
    private Double belt;
    @Nullable
    private Double hip;
    @Nullable
    private Double thigh;
    @Nullable
    private Double calf;
    @Nullable
    private Double weight;
    private Double height;
}
