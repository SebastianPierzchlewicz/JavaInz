package pl.inz.praca.WsbPracaInz.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.model.BodySize;
import pl.inz.praca.WsbPracaInz.repo.BodySizeRepo;

@Repository
 interface BodySizeSQLRepo extends BodySizeRepo, JpaRepository<BodySize, Long> {
}
