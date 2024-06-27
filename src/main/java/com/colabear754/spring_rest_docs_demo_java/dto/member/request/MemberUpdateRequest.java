package com.colabear754.spring_rest_docs_demo_java.dto.member.request;

public record MemberUpdateRequest(
        String password,
        String newPassword,
        String name,
        Integer age
) {
}
