package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.dto.ApiResponse;
import com.colabear754.spring_rest_docs_demo_java.security.AdminAuthorize;
import com.colabear754.spring_rest_docs_demo_java.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@AdminAuthorize
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/members")
    public ApiResponse getAllMembers() {
        return ApiResponse.success(adminService.getMembers());
    }

    @GetMapping("/admins")
    public ApiResponse getAllAdmins() {
        return ApiResponse.success(adminService.getAdmins());
    }
}
