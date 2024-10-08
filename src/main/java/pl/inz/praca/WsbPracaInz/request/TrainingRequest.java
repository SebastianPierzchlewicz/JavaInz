package pl.inz.praca.WsbPracaInz.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@AllArgsConstructor
@Getter
public class TrainingRequest {

    @Nullable
    private Long id;
    private Long exerciseId;
    @Min(1)
    @Max(10)
    private Integer difficulty;
    private List<SeriesDto> series;
    @Getter
    @AllArgsConstructor
    public static class SeriesDto {
        @Nullable
        private Long id;
        private double amount;
        @Nullable
        private Double weight;
    }
}
