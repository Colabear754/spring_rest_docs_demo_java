package com.colabear754.spring_rest_docs_demo_java.services;

import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_in.request.SignInRequest;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_in.response.SignInResponse;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_up.request.SignUpRequest;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_up.response.SignUpResponse;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRefreshTokenRepository;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SignServiceTest {
    private final SignService signService;
    private final MemberRepository memberRepository;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final PasswordEncoder encoder;

    @Autowired
    SignServiceTest(SignService signService, MemberRepository memberRepository, MemberRefreshTokenRepository memberRefreshTokenRepository, PasswordEncoder encoder) {
        this.signService = signService;
        this.memberRepository = memberRepository;
        this.memberRefreshTokenRepository = memberRefreshTokenRepository;
        this.encoder = encoder;
    }

    @BeforeEach
    @AfterEach
    void clear() {
        memberRefreshTokenRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }
    
    @Test
    void 회원가입() {
        // given
        SignUpRequest request = new SignUpRequest("colabear754", "1234", "콜라곰", 30);
        // when
        SignUpResponse response = signService.registMember(request);
        // then
        assertThat(response.account()).isEqualTo("colabear754");
        assertThat(response.name()).isEqualTo("콜라곰");
        assertThat(response.age()).isEqualTo(30);
    }

    @Test
    void 아이디는_중복될_수_없다() {
        // given
        memberRepository.save(Member.builder()
                .account("colabear754")
                .password("1234")
                .build());
        SignUpRequest request = new SignUpRequest("colabear754", "1234", null, null);
        // when
        // then
        Assertions.assertThatThrownBy(() -> signService.registMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용중인 아이디입니다.");
    }

    @Test
    void 로그인() {
        // given
        memberRepository.save(Member.builder()
                .account("colabear754")
                .password(encoder.encode("1234"))
                .name("콜라곰")
                .type(MemberType.USER)
                .build());
        // when
        SignInResponse response = signService.signIn(new SignInRequest("colabear754", "1234"));
        // then
        assertThat(response.name()).isEqualTo("콜라곰");
        assertThat(response.type()).isEqualTo(MemberType.USER);
    }

    @Test
    void 로그인실패() {
        // given
        memberRepository.save(Member.builder()
                .account("colabear754")
                .password("1234")
                .build());
        // when
        // then
        Assertions.assertThatThrownBy(() -> signService.signIn(new SignInRequest("colabear754", "12345")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
}