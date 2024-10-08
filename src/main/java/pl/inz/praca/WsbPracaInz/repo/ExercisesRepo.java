package pl.inz.praca.WsbPracaInz.repo;

import org.springframework.data.domain.PageRequest;
import pl.inz.praca.WsbPracaInz.model.Exercises;

import java.util.List;
import java.util.Optional;

public interface ExercisesRepo
{

    Exercises save(Exercises toSave);

    Optional<Exercises> findById(long id);

    List<Exercises> findAll();

    List<Exercises> findAllBy(PageRequest pageRequest);

    void removeById(long id);

}
