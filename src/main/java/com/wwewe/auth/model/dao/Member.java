package com.wwewe.auth.model.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Entity
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @GeneratedValue
    Long id;

    @NaturalId
    String email;

    String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<AccessToken> accessTokens;

    @Builder(toBuilder = true)
    private Member(String email, String username) {
        this.email = email;
        this.username= username;
        this.id = null;
        this.accessTokens = null;
    }
}
