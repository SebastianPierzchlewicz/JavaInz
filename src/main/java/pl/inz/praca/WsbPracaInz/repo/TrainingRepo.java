package pl.inz.praca.WsbPracaInz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.inz.praca.WsbPracaInz.model.Training;

import java.util.Optional;

public interface TrainingRepo
{
    Training save(Training training);

    Optional<Training> findById(final Long id);

    void deleteById(Long aLong);
}
