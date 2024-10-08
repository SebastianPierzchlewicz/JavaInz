package pl.inz.praca.WsbPracaInz.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.inz.praca.WsbPracaInz.model.BodySize;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.model.Training;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity @Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;
    private boolean verification;
    private boolean banned;
    private boolean firstJoin;
    private boolean takeForm;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();


    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Exercises> exercises; //cwiczenia

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Training> trainings; //treningi

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<BodySize> bodySizes; //pomiary ciala
    @PrePersist
    void PrePersist(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Collection<GrantedAuthority> roleToAuthority() {
        final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        this.roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.getName())));
        return grantedAuthorities;
    }

    public void addExercises(Exercises exercises) {
        this.exercises.add(exercises);
    }

    public void removeExercises(Long id) {
        this.exercises.removeIf(exercises1 -> exercises1.getId().equals(id));
    }

    public void removeExercises(Exercises exercises) {
        this.removeExercises(exercises.getId());
    }

    public Exercises getExercise(Long id) {
        return this.exercises.stream().filter(exercises1 -> exercises1.getId().equals(id)).findFirst().orElse(null);
    }

    public void addTraining(final Training training) {
        this.trainings.add(training);
    }

    public Training getTraining(Long id) {
        return this.trainings.stream().filter(training -> training.getId().equals(id)).findFirst().orElse(null);
    }

    public void removeTraining(Long id) {
        this.trainings.removeIf(training -> training.getId().equals(id));
    }
    public void removeTraining(final Training training) {
        this.removeTraining(training.getId());
    }

    public void addBodySize(final BodySize bodySize) {
        this.bodySizes.add(bodySize);
    }

    public BodySize getBodySize(Long id) {
        return this.bodySizes.stream().filter(training -> training.getId().equals(id)).findFirst().orElse(null);
    }

    public void removeBodySize(Long id) {
        this.bodySizes.removeIf(training -> training.getId().equals(id));
    }
    public void removeBodySize(final BodySize bodySize) {
        this.removeBodySize(bodySize.getId());
    }

    public Exercises getExerciseByName(final String name) {
        return this.getExercises().stream().filter(exercises1 -> exercises1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
