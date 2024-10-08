package pl.inz.praca.WsbPracaInz.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.repo.TrainingRepo;

@Repository
 interface TrainingSQLRepo extends
 TrainingRepo, JpaRepository<Training, Long> {
}
