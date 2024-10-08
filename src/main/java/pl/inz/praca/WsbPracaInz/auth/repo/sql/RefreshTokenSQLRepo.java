package pl.inz.praca.WsbPracaInz.auth.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.auth.model.RefreshToken;
import pl.inz.praca.WsbPracaInz.auth.repo.RefreshTokenRepo;

@Repository
interface RefreshTokenSQLRepo extends RefreshTokenRepo, JpaRepository<RefreshToken, Long> {



}
