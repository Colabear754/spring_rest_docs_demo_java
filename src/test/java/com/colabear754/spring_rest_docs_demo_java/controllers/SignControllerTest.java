package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.common.ApiStatus;
import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_in.request.SignInRequest;
import com.colabear754.spring_rest_docs_demo_java.dto.sign_up.request.SignUpRequest;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;
import com.colabear754.spring_rest_docs_demo_java.preprocessor.DocumentExtension;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRefreshTokenRepository;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.demo.com", uriPort = 0)
public class SignControllerTest {
    private final MemberRepository memberRepository;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MockMvc mockMvc;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SignControllerTest(MemberRepository memberRepository, MemberRefreshTokenRepository memberRefreshTokenRepository, MockMvc mockMvc, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.memberRefreshTokenRepository = memberRefreshTokenRepository;
        this.mockMvc = mockMvc;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void clear() {
        memberRefreshTokenRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 회원가입() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("colabear754", "1234", "콜라곰", 99);
        // when
        ResultActions action = mockMvc.perform(post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.account", Matchers.is("colabear754")))
                .andExpect(jsonPath("$.data.name", Matchers.is("콜라곰")))
                .andExpect(jsonPath("$.data.age", Matchers.is(99)));

        // Spring REST Docs
        action.andDo(document("sign-up",
                DocumentExtension.requestPreprocessor(),
                DocumentExtension.responsePreprocessor(),
                requestFields(
                        fieldWithPath("account").description("사용자 계정"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("name").description("사용자 이름").optional(),
                        fieldWithPath("age").description("사용자 나이").optional()
                ),
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data").description("응답 데이터"),
                        fieldWithPath("data.id").description("사용자 ID"),
                        fieldWithPath("data.account").description("사용자 계정"),
                        fieldWithPath("data.name").description("사용자 이름").optional(),
                        fieldWithPath("data.age").description("사용자 나이").optional()
                )));
    }

    @Test
    void 로그인() throws Exception {
        // given
        memberRepository.save(Member.builder()
                .account("colabear754")
                .password(passwordEncoder.encode("1234"))
                .name("콜라곰")
                .type(MemberType.USER)
                .build());
        SignInRequest request = new SignInRequest("colabear754", "1234");
        // when
        ResultActions action = mockMvc.perform(post("/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.name", Matchers.is("콜라곰")))
                .andExpect(jsonPath("$.data.type", Matchers.is(MemberType.USER.name())))
                .andExpect(jsonPath("$.data.accessToken", Matchers.notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", Matchers.notNullValue()));

        // Spring REST Docs
        action.andDo(document("sign-in",
                DocumentExtension.requestPreprocessor(),
                DocumentExtension.responsePreprocessor(),
                requestFields(
                        fieldWithPath("account").description("사용자 계정"),
                        fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data").description("응답 데이터"),
                        fieldWithPath("data.name").description("사용자 이름"),
                        fieldWithPath("data.type").description("사용자 유형"),
                        fieldWithPath("data.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.refreshToken").description("리프레시 토큰")
                )));
    }
}
