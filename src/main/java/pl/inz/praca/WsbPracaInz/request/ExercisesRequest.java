package pl.inz.praca.WsbPracaInz.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class ExercisesRequest
{

    @Nullable
    private Long id;
    @NotBlank
    private final String name;
    @Nullable
    @Max(10)
    @Min(1)
    private final Integer difficulty;
    @Nullable
    private final String seriesAmount;
}
