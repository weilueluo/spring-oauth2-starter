package com.wwewe.auth.configuration;

import com.auth0.jwt.HeaderParams;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class JwtConfig {

    // for generating hmac secret
//    static {
//        try {
//            Files.write(Path.of("hmac-secret.txt"), new SecureRandom().generateSeed(256));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Value("${wwewe.jwt.hmac-secret-file}")
    private Resource secretFileResource;
    private byte[] secret;

    @PostConstruct
    public void decodeSecret() throws IOException {
        secret = FileUtils.readFileToByteArray(secretFileResource.getFile());
    }

    @Bean
    @Qualifier("hmacSecret")
    public byte[] hmacSecret() {
        return secret;
    }


    // below are for oauth2ResourceServer to decode jwt in authentication filter
    @Bean
    public JWTVerifier jwtVerifier(@Qualifier("hmacSecret") byte[] hmacSecret) {
        Algorithm algorithm = Algorithm.HMAC256(hmacSecret);
        return JWT.require(algorithm).build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWTVerifier jwtVerifier) {
        return (token) -> {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return Jwt.withTokenValue(decodedJWT.getToken())
                    .headers(headers -> {
                        headers.put(HeaderParams.ALGORITHM, decodedJWT.getAlgorithm());
                        headers.put(HeaderParams.KEY_ID, decodedJWT.getKeyId());
                        headers.put(HeaderParams.TYPE, decodedJWT.getType());
                        headers.put(HeaderParams.CONTENT_TYPE, decodedJWT.getContentType());
                    })
                    .claims(claims -> decodedJWT.getClaims().forEach((key, claim) -> {
                        String value = Optional.ofNullable(claim.asString())
                                        .orElse(claim.toString());
                        claims.put(key, value);
                    }))
                    .issuedAt(decodedJWT.getIssuedAtAsInstant())
                    .expiresAt(decodedJWT.getExpiresAtAsInstant())
                    .build();
        };
    }
}
