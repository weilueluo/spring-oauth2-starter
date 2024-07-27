package com.wwewe.auth.service;

import com.wwewe.auth.model.SignupResult;
import com.wwewe.auth.model.dao.AccessToken;
import com.wwewe.auth.model.dao.Member;
import com.wwewe.auth.repository.AccessTokenRepository;
import com.wwewe.auth.repository.MemberRepository;
import com.wwewe.auth.service.statemachine.EmailSignupStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final EmailSignupStateMachine emailSignupStateMachine;

    private final MemberRepository memberRepository;
    private final AccessTokenRepository accessTokenRepository;

    public SignupResult signup(String email, String otpValue) {
        EmailSignupStateMachine.OutputState signupState = this.emailSignupStateMachine.run(email, otpValue);
        switch (signupState) {
            case DONE -> {
                final Member member = memberRepository.createByEmail(email);
                final AccessToken accessToken = accessTokenRepository.createByMember(member);
                return SignupResult.builder().status(SignupResult.Status.DONE).accessToken(accessToken).build();
            }
            case WAITING_FOT_OTP -> {
                return SignupResult.builder().status(SignupResult.Status.WAITING_FOR_OTP).build();
            }
            default -> throw new IllegalStateException("Invalid signup state");
        }
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
