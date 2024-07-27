package com.wwewe.auth.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;

@Entity
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailOtp {

    @Id
    String email;
    String value;
    Integer expirationInSeconds;
    Instant createdAt;
    boolean verified;

    @JsonIgnore
    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plus(Duration.ofSeconds(expirationInSeconds)));
    }
}
