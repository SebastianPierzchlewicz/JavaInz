package pl.inz.praca.WsbPracaInz.auth.security.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil
{

    private static final String SECRET = "sgFqtv6msgJc8xb8bkE3YGfuVA2YmepnYpQPjTf";
    private static final int EXPIRED = 30;

    public String buildJwt(UserDetails user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRED* 60 * 1000))
                .withIssuer(new Date(System.currentTimeMillis()).toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public DecodedJWT validToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String decode(final String token) {
        DecodedJWT decode = JWT.decode(token);
        Base64.Decoder decoder = Base64.getUrlDecoder();

        return new String(decoder.decode(decode.getPayload()));
    }


    public String getUsernameFromToken(String token) {
        return validToken(token).getSubject();
    }
}
