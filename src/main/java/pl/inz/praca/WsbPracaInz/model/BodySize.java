package pl.inz.praca.WsbPracaInz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;
import pl.inz.praca.WsbPracaInz.request.BodySizeRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bodySize")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class
BodySize
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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
    private Double weight;
    private Double height;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BodySize(BodySizeRequest request) {
        this.chest = request.getChest();
        this.neck = request.getNeck();
        this.biceps = request.getBiceps();
        this.waist = request.getWaist();
        this.belt = request.getBelt();
        this.hip = request.getHip();
        this.thigh = request.getThigh();
        this.calf = request.getCalf();
        this.weight = request.getWeight();
        this.height = request.getHeight();
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

    public Double getBmi() {
        return MathHelper.countBmi(this.weight, this.height);
    }
}
