package com.colabear754.spring_rest_docs_demo_java;

import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        memberRepository.save(Member.builder()
                .account("admin")
                .password(encoder.encode("admin"))
                .name("관리자")
                .type(MemberType.ADMIN)
                .build());
    }
}
