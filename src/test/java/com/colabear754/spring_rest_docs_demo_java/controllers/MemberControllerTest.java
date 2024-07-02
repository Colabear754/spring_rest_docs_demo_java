package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.common.ApiStatus;
import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.dto.member.request.MemberUpdateRequest;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;
import com.colabear754.spring_rest_docs_demo_java.preprocessor.DocumentExtension;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import com.colabear754.spring_rest_docs_demo_java.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.demo.com", uriPort = 0)
public class MemberControllerTest {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MockMvc mockMvc;
    private final PasswordEncoder encoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MemberControllerTest(MemberRepository memberRepository, TokenProvider tokenProvider, MockMvc mockMvc, PasswordEncoder encoder) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.mockMvc = mockMvc;
        this.encoder = encoder;
    }

    @BeforeEach
    void clear() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 로그인한_사용자_정보_조회() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .account("colabear754")
                .password("1234")
                .name("콜라곰")
                .type(MemberType.USER)
                .build());
        String token = tokenProvider.createAccessToken(String.format("%s:%s", member.getId(), MemberType.USER));
        // when
        ResultActions action = mockMvc.perform(get("/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.id", Matchers.is(member.getId().toString())))
                .andExpect(jsonPath("$.data.account", Matchers.is("colabear754")))
                .andExpect(jsonPath("$.data.name", Matchers.is("콜라곰")))
                .andExpect(jsonPath("$.data.age", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.type", Matchers.is("USER")))
                .andExpect(jsonPath("$.data.createdAt", Matchers.notNullValue()));

        // Spring REST Docs
        action.andDo(document("member/info",
                DocumentExtension.requestPreprocessor(modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer {access_token}")),
                DocumentExtension.responsePreprocessor(),
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data.id").description("사용자 ID"),
                        fieldWithPath("data.account").description("계정"),
                        fieldWithPath("data.name").description("이름").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data.age").description("나이").type(JsonFieldType.NUMBER).optional(),
                        fieldWithPath("data.type").description("사용자 타입"),
                        fieldWithPath("data.createdAt").description("가입 일시")
                )));
    }

    @Test
    void 로그인한_사용자_탈퇴() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .account("colabear754")
                .password("1234")
                .name("콜라곰")
                .type(MemberType.USER)
                .build());
        String token = tokenProvider.createAccessToken(String.format("%s:%s", member.getId(), MemberType.USER));
        // when
        ResultActions action = mockMvc.perform(delete("/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.result", Matchers.is(true)));

        // Spring REST Docs
        action.andDo(document("member/delete",
                DocumentExtension.requestPreprocessor(modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer {access_token}")),
                DocumentExtension.responsePreprocessor(),
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data.result").description("탈퇴 결과")
                )));
    }

    @Test
    void 로그인한_사용자_정보_수정() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .account("colabear754")
                .password(encoder.encode("1234"))
                .name("콜라곰")
                .type(MemberType.USER)
                .build());
        String token = tokenProvider.createAccessToken(String.format("%s:%s", member.getId(), MemberType.USER));
        MemberUpdateRequest request = new MemberUpdateRequest("1234", null, "콜라곰곰", 100);
        // when
        ResultActions action = mockMvc.perform(put("/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data.result", Matchers.is(true)))
                .andExpect(jsonPath("$.data.name", Matchers.is("콜라곰곰")))
                .andExpect(jsonPath("$.data.age", Matchers.is(100)));

        // Spring REST Docs
        action.andDo(document("member/update",
                DocumentExtension.requestPreprocessor(modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer {access_token}")),
                DocumentExtension.responsePreprocessor(),
                requestFields(
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("newPassword").description("새 비밀번호").optional(),
                        fieldWithPath("name").description("이름").optional(),
                        fieldWithPath("age").description("나이").optional()
                ),
                responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data.result").description("수정 결과"),
                        fieldWithPath("data.name").description("이름").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data.age").description("나이").type(JsonFieldType.NUMBER).optional()
                )));
    }
}