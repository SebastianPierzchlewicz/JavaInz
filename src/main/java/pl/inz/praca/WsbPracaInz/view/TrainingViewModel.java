package pl.inz.praca.WsbPracaInz.view;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.request.TrainingRequest;

import java.time.LocalDateTime;
import java.util.List;
@Getter
public class TrainingViewModel
{
    private final Long id;
    private final Integer difficulty;
    private final String name;
    private final List<TrainingRequest.SeriesDto> series;
    private final LocalDateTime createdAt;


    public TrainingViewModel(final Training training) {
        this.id = training.getId();
        this.difficulty = training.getDifficulty();
        this.name = training.getExercises().getName();
        this.series = training.getSeries().stream().map(series1 -> new TrainingRequest.SeriesDto(series1.getId(), series1.getAmount(), series1.getWeight())).toList();
        this.createdAt = training.getCreatedAt();
    }
}
