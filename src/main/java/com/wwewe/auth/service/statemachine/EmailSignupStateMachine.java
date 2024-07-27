package com.wwewe.auth.service.statemachine;

import com.tsm4j.core.StateMachine;
import com.tsm4j.core.StateMachineBuilder;
import com.tsm4j.core.StateMachineContext;
import com.wwewe.auth.exceptions.BusinessException;
import com.wwewe.auth.model.dao.EmailOtp;
import com.wwewe.auth.repository.MemberRepository;
import com.wwewe.auth.service.OtpService;
import com.wwewe.auth.utils.EmailUtils;
import com.wwewe.auth.utils.StateMachineUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Scope("prototype")
public class EmailSignupStateMachine {

    private final MemberRepository memberRepository;

    private final OtpService otpService;

    private String email;
    private String otpValue;

    public OutputState run(String email, String otpValue) {
        this.email = email;
        this.otpValue = otpValue;

        StateMachine<State> stateMachine = StateMachineBuilder.from(State.class)
                .addTransition(State.INIT, State.VALIDATE_EMAIL)
                .addListener(State.VALIDATE_EMAIL, this::validateEmail)
                .addTransition(State.VALIDATE_EMAIL_DONE, State.CHECK_USER_EXISTS)
                .addListener(State.CHECK_USER_EXISTS, this::checkUserExists)
                .addTransition(State.CHECK_USER_EXISTS_DONE, State.VERIFY_OTP)
                .addListener(State.VERIFY_OTP, this::verifyOtp)
                .addListener(State.GENERATE_AND_SEND_OTP, this::generateAndSendOtp)
                .addListener(StateMachineUtils.debugLoggingListener())
                .build();

        StateMachineContext<State> context = stateMachine.send(State.INIT);

        return switch (context.getLatestState()) {
            case VERIFY_OTP_DONE -> OutputState.DONE;
            case GENERATE_AND_SEND_OTP_DONE -> OutputState.WAITING_FOT_OTP;
            default ->
                    throw new IllegalStateException("Invalid signup end state, email=" + email + "otpValue=" + otpValue);
        };
    }

    private void validateEmail(StateMachineContext<State> context) {
        if (!EmailUtils.isValid(this.email)) {
            throw new BusinessException("Invalid email: " + email);
        }
        context.queue(State.VALIDATE_EMAIL_DONE);
    }

    private void checkUserExists(StateMachineContext<State> context) {
        memberRepository.findByEmail(this.email).ifPresent((member) -> {
            throw new BusinessException("This email is already registered with a user: " + member.getId());
        });
        context.queue(State.CHECK_USER_EXISTS_DONE);
    }

    private void verifyOtp(StateMachineContext<State> context) {
        Optional<EmailOtp> otp = otpService.findByEmail(this.email);
        if (otp.isEmpty() || otp.get().isExpired()) {
            context.queue(State.GENERATE_AND_SEND_OTP);
        } else if (otp.get().getValue().equals(this.otpValue)) {
            otpService.update(otp.get().toBuilder().verified(true).build());
            context.queue(State.VERIFY_OTP_DONE);
        } else {
            throw new BusinessException("Invalid OTP");
        }
    }

    private void generateAndSendOtp(StateMachineContext<State> context) {
        EmailOtp otp = this.otpService.createByEmail(this.email);
        // TODO: send otp to user email
        context.queue(State.GENERATE_AND_SEND_OTP_DONE);
    }

    public enum OutputState {
        DONE,
        WAITING_FOT_OTP
    }

    private enum State {
        INIT, VALIDATE_EMAIL,
        VALIDATE_EMAIL_DONE, CHECK_USER_EXISTS,
        CHECK_USER_EXISTS_DONE, VERIFY_OTP,
        VERIFY_OTP_DONE, CREATE_USER,
        CREATE_USER_DONE, GENERATE_AND_SEND_OTP,
        GENERATE_AND_SEND_OTP_DONE, DONE
    }
}
