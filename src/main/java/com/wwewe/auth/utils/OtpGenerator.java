package com.wwewe.auth.utils;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
public class OtpGenerator {

    private static final int OTP_LENGTH = 6;

    public String create() {
        return create(OTP_LENGTH);
    }

    private String create(int length) {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, length)
                .forEach(i -> builder.append(new Random().nextInt(0, 10)));
        return builder.toString();
    }
}
