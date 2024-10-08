package pl.inz.praca.WsbPracaInz.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.inz.praca.WsbPracaInz.auth.model.RefreshToken;
import pl.inz.praca.WsbPracaInz.auth.repo.RefreshTokenRepo;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {


    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public Optional<RefreshToken> findByToken(final String token) {
        return this.refreshTokenRepo.findByToken(token);
    }

    public RefreshToken createRefreshToken(final String username) {
        return this.refreshTokenRepo.save(
                new RefreshToken(null, UUID.randomUUID().toString(), username, LocalDateTime.now().plusDays(30))
        );
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpired().compareTo(LocalDateTime.now()) < 0) {
            refreshTokenRepo.deleteById(token.getId());
            return null;
        }
        return token;
    }


    public void deleteByUsername(String username) {
         refreshTokenRepo.deleteByUsername(username);
    }


}
