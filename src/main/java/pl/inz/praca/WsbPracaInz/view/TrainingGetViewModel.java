package pl.inz.praca.WsbPracaInz.view;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.request.TrainingRequest;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TrainingGetViewModel
{
    private final Long id;
    private final Integer difficulty;
    private final Long exerciseId;
    private final List<TrainingRequest.SeriesDto> series;
    private final LocalDateTime createdAt;


    public TrainingGetViewModel(final Training training) {
        this.id = training.getId();
        this.difficulty = training.getDifficulty();
        this.exerciseId = training.getExercises().getId();
        this.series = training.getSeries().stream().map(series1 -> new TrainingRequest.SeriesDto(series1.getId(), series1.getAmount(), series1.getWeight())).toList();
        this.createdAt = training.getCreatedAt();
    }
}
