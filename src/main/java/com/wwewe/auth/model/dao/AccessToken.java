package com.wwewe.auth.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessToken {

    @Id
    @GeneratedValue
    Long id;

    String value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Member member;

    @Builder(toBuilder = true)
    private AccessToken(String value, Member member) {
        this.value = value;
        this.member = member;
        this.id = null;
    }
}
