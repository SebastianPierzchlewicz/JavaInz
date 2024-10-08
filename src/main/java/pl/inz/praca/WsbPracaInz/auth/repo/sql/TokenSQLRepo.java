package pl.inz.praca.WsbPracaInz.auth.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.auth.model.Token;
import pl.inz.praca.WsbPracaInz.auth.repo.TokenRepo;


@Repository
interface TokenSQLRepo extends TokenRepo, JpaRepository<Token, Long> {



}
