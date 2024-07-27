package com.wwewe.auth.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wwewe.auth.model.SignupResult;
import com.wwewe.auth.model.dao.AccessToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupResultDto {
    Status status;
    AccessToken accessToken;

    public enum Status {
        WAITING_FOR_OTP,
        DONE
    }


    public static SignupResultDto from(SignupResult signupResult) {
        return SignupResultDto.builder()
                .status(convertStatus(signupResult.getStatus()))
                .accessToken(signupResult.getAccessToken())
                .build();
    }

    private static Status convertStatus(SignupResult.Status status) {
        return switch (status) {
            case WAITING_FOR_OTP -> Status.WAITING_FOR_OTP;
            case DONE -> Status.DONE;
        };
    }
}
