package pl.inz.praca.WsbPracaInz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import pl.inz.praca.WsbPracaInz.request.ExercisesRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Exercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Nullable
    private Integer difficulty;
    @Nullable
    private String seriesAmount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public Exercises(ExercisesRequest request) {
        this.id= null;
        this.name = request.getName();
        this.difficulty = request.getDifficulty();
        this.seriesAmount = request.getSeriesAmount();
    }

    @PrePersist
    void PrePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
