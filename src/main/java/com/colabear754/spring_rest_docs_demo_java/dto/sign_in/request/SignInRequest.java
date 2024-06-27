package com.colabear754.spring_rest_docs_demo_java.dto.sign_in.request;

public record SignInRequest(
        String account,
        String password
) {
}
