package pl.inz.praca.WsbPracaInz.auth.repo;


import pl.inz.praca.WsbPracaInz.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepo
//extends JpaRepository<RefreshToken, Long>

{
    Optional<RefreshToken> findByToken(String token);

    void deleteById(final Long id);
    void deleteByUsername(final String username);

    RefreshToken save(RefreshToken token);




}
