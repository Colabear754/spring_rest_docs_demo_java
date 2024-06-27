package com.colabear754.spring_rest_docs_demo_java.dto.sign_in.response;

import com.colabear754.spring_rest_docs_demo_java.common.MemberType;

public record SignInResponse(
        String name,
        MemberType type,
        String accessToken,
        String refreshToken
) {}
