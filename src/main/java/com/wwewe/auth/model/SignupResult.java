package com.wwewe.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wwewe.auth.model.dao.AccessToken;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SignupResult {
    Status status;
    AccessToken accessToken;

    public enum Status {
        WAITING_FOR_OTP,
        DONE
    }
}
