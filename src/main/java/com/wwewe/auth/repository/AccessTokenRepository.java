package com.wwewe.auth.repository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.wwewe.auth.model.dao.AccessToken;
import com.wwewe.auth.model.dao.Member;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
@RequiredArgsConstructor
public class AccessTokenRepository {

    private final static Duration EXPIRE_DURATION = Duration.of(6, ChronoUnit.HOURS);
    private final SessionFactory sessionFactory;
    private final byte[] hmacSecret;


    public AccessToken createByMember(Member member) {
        Instant now = Instant.now();
        String jwt = JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(now.plus(EXPIRE_DURATION))
                .withClaim(Claims.SCOPE, Scopes.MEMBER)
                .withClaim(Claims.MID, member.getId())
                .sign(Algorithm.HMAC256(hmacSecret));

        AccessToken accessToken = AccessToken.builder()
                .value(jwt)
                .member(member)
                .build();

        sessionFactory.inTransaction(session -> session.persist(accessToken));

        return accessToken;
    }

    public static class Claims {
        public static final String SCOPE = "scp";
        public static final String MID = "mid";
    }

    public static class Scopes {
        public static final String MEMBER = "member";
    }
}
