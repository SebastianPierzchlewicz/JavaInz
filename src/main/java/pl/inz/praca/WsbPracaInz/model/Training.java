package pl.inz.praca.WsbPracaInz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "training")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nullable
    private Integer difficulty;
    @ManyToOne(cascade = CascadeType.ALL)
    private Exercises exercises;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Series> series;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Training(@Nullable Integer difficulty, Exercises exercises, List<Series> series) {
        this.difficulty = difficulty;
        this.exercises = exercises;
        this.series = series;
    }

    @PrePersist
    void PrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Double countPowerProgress() {
        final double sum = this.getSeries().stream()
                .filter(series1 -> series1.getWeight() != null)
                .filter(series1 -> series1.getAmount() > 0)
                .filter(series1 -> series1.getWeight() > 0)
                .map(series -> (series.getAmount() * series.getWeight())).reduce(0.0,Double::sum);


        final double amounts = this.getSeries().stream()
                .filter(series1 -> series1.getWeight() != null)
                .filter(series1 -> series1.getAmount() > 0)
                .filter(series1 -> series1.getWeight() > 0)
                .map(Series::getAmount).reduce(0.0,Double::sum);

        return MathHelper.round(sum/amounts,2);
    }
}
