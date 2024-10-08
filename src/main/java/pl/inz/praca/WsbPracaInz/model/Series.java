package pl.inz.praca.WsbPracaInz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;
import pl.inz.praca.WsbPracaInz.request.TrainingRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "series")
@NoArgsConstructor
@Data
@AllArgsConstructor
@ToString
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    @Nullable
    private Double weight;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Series(TrainingRequest.SeriesDto seriesDto) {
        this.weight = seriesDto.getWeight();
        this.amount = seriesDto.getAmount();
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
