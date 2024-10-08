package pl.inz.praca.WsbPracaInz.auth.repo;


import pl.inz.praca.WsbPracaInz.auth.model.Token;

import java.util.Optional;

public interface TokenRepo


{
    Optional<Token> findTokenByToken(String token);

    void deleteTokenByToken(String token);

    void deleteById(Long id);
    Token save(Token token);

    void deleteTokenByTokenAndUsername(String token, String username);

    Optional<Token> findTokenByTokenAndUsername(String token, String username);


}
