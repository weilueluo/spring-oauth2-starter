package com.wwewe.auth.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wwewe.auth.model.dao.Member;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Optional;

@Builder(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberDto {

    Long id;
    String email;
    String username;

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .id(member.getId())
                .username(member.getUsername())
                .build();
    }

    public static Optional<MemberDto> from(Optional<Member> member) {
        return member.map(MemberDto::from);
    }
}
