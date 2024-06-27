package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.dto.ApiResponse;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_in.request.SignInRequest;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_up.request.SignUpRequest;
import com.colabear754.spring_rest_docs_demo_java.services.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class SignController {
    private final SignService signService;

    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody SignUpRequest request) {
        return ApiResponse.success(signService.registMember(request));
    }

    @PostMapping("/sign-in")
    public ApiResponse signIn(@RequestBody SignInRequest request) {
        return ApiResponse.success(signService.signIn(request));
    }
}
