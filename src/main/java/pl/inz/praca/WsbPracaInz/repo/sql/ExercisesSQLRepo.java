package pl.inz.praca.WsbPracaInz.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.repo.ExercisesRepo;

@Repository
 interface ExercisesSQLRepo extends ExercisesRepo, JpaRepository<Exercises, Long> {

}
