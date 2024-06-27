package com.colabear754.spring_rest_docs_demo_java.dto.sign_up.response;

import com.colabear754.spring_rest_docs_demo_java.entity.Member;

import java.util.UUID;

public record SignUpResponse(
        UUID id,
        String account,
        String name,
        Integer age
) {
    public static SignUpResponse from(Member member) {
        return new SignUpResponse(
                member.getId(),
                member.getAccount(),
                member.getName(),
                member.getAge()
        );
    }
}
