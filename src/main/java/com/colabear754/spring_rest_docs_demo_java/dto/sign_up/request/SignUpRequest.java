package com.colabear754.spring_rest_docs_demo_java.dto.sign_up.request;

public record SignUpRequest(
        String account,
        String password,
        String name,
        Integer age
) {
}
