package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.dto.ApiResponse;
import com.colabear754.spring_rest_docs_demo_java.dto.member.request.MemberUpdateRequest;
import com.colabear754.spring_rest_docs_demo_java.security.UserAuthorize;
import com.colabear754.spring_rest_docs_demo_java.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@UserAuthorize
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ApiResponse getMemberInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.success(memberService.getMemberInfo(UUID.fromString(user.getUsername())));
    }

    @DeleteMapping
    public ApiResponse deleteMember(@AuthenticationPrincipal User user) {
        return ApiResponse.success(memberService.deleteMember(UUID.fromString(user.getUsername())));
    }

    @PutMapping
    public ApiResponse updateMember(@AuthenticationPrincipal User user, @RequestBody MemberUpdateRequest request) {
        return ApiResponse.success(memberService.updateMember(UUID.fromString(user.getUsername()), request));
    }
}
