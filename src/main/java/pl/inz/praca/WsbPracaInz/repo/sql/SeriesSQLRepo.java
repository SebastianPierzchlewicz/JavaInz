package pl.inz.praca.WsbPracaInz.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.model.Series;
import pl.inz.praca.WsbPracaInz.repo.SeriesRepo;

@Repository
interface SeriesSQLRepo extends SeriesRepo, JpaRepository<Series,Long> {
}
