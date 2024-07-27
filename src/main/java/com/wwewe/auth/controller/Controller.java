package com.wwewe.auth.controller;


import com.wwewe.auth.model.dao.Member;
import com.wwewe.auth.model.dto.MemberDto;
import com.wwewe.auth.model.dto.SignupResultDto;
import com.wwewe.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final MemberService memberService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello!");
    }

    @GetMapping("/hello/member")
    @PreAuthorize("hasAuthority('SCOPE_member')")
    public ResponseEntity<String> helloMember() {
        return ResponseEntity.ok("hello member!");
    }

    @PostMapping("/member/signup")
    public ResponseEntity<SignupResultDto> signup(@RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(SignupResultDto.from(memberService.signup(signupRequest.getEmail(), signupRequest.getOtp())));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long memberId) {
        Optional<Member> member = this.memberService.findById(memberId);
        return ResponseEntity.of(MemberDto.from(member));
    }

    @GetMapping("/member/email/{email}")
    public ResponseEntity<MemberDto> getMemberByEmail(@PathVariable String email) {
        Optional<Member> member = this.memberService.findByEmail(email);
        return ResponseEntity.of(MemberDto.from(member));
    }
}
