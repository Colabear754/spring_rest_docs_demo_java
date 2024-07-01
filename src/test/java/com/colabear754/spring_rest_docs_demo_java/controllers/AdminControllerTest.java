package com.colabear754.spring_rest_docs_demo_java.controllers;

import com.colabear754.spring_rest_docs_demo_java.common.ApiStatus;
import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;
import com.colabear754.spring_rest_docs_demo_java.preprocessor.DocumentExtension;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import com.colabear754.spring_rest_docs_demo_java.security.TokenProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.demo.com", uriPort = 0)
public class AdminControllerTest {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final MockMvc mockMvc;

    @Autowired
    public AdminControllerTest(TokenProvider tokenProvider, MemberRepository memberRepository, MockMvc mockMvc) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void clear() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 모든_사용자_찾기() throws Exception {
        // given
        String token = "Bearer " + tokenProvider.createAccessToken("admin:ADMIN");
        List<String> uuids = memberRepository.saveAll(List.of(
                        Member.builder()
                                .account("colabear754")
                                .password("1234")
                                .name("콜라곰")
                                .type(MemberType.USER)
                                .build(),
                        Member.builder()
                                .account("ciderbear754")
                                .password("1234")
                                .name("사이다곰")
                                .type(MemberType.USER)
                                .build(),
                        Member.builder()
                                .account("fantabear754")
                                .password("1234")
                                .name("환타곰")
                                .type(MemberType.USER)
                                .build()
                )).stream()
                .map(member -> member.getId().toString())
                .toList();
        // when
        ResultActions action = mockMvc.perform(get("/admin/members").header(HttpHeaders.AUTHORIZATION, token));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.data[*].id", Matchers.containsInAnyOrder(uuids.get(0), uuids.get(1), uuids.get(2))))
                .andExpect(jsonPath("$.data[*].name", Matchers.containsInAnyOrder("콜라곰", "사이다곰", "환타곰")))
                .andExpect(jsonPath("$.data[*].type", Matchers.containsInAnyOrder("USER", "USER", "USER")));

        // Spring REST Docs
        action.andDo(document("admin/members",
                DocumentExtension.requestPreprocessor(modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer {access_token}")),
                DocumentExtension.responsePreprocessor(),
                memberInfoResponseSnippet()
        ));
    }

    @Test
    void 모든_관리자_찾기() throws Exception {
        // given
        memberRepository.save(Member.builder()
                .account("admin")
                .password("1234")
                .name("관리자")
                .type(MemberType.ADMIN)
                .build());
        String token = "Bearer " + tokenProvider.createAccessToken("admin:ADMIN");
        String uuid = memberRepository.findByAccount("admin").map(Member::getId).map(UUID::toString).orElse("");
        // when
        ResultActions action = mockMvc.perform(get("/admin/admins").header(HttpHeaders.AUTHORIZATION, token));
        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.is(ApiStatus.SUCCESS.name())))
                .andExpect(jsonPath("$.message", Matchers.nullValue()))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[*].id", Matchers.containsInAnyOrder(uuid)))
                .andExpect(jsonPath("$.data[*].name", Matchers.containsInAnyOrder("관리자")))
                .andExpect(jsonPath("$.data[*].type", Matchers.containsInAnyOrder("ADMIN")));

        // Spring REST Docs
        action.andDo(document("admin/admins",
                DocumentExtension.requestPreprocessor(modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer {access_token}")),
                DocumentExtension.responsePreprocessor(),
                memberInfoResponseSnippet()
        ));
    }

    private ResponseFieldsSnippet memberInfoResponseSnippet() {
        return responseFields(
                fieldWithPath("status").description("API 상태"),
                fieldWithPath("message").description("API 메시지").type(JsonFieldType.STRING).optional(),
                fieldWithPath("data").description("응답 데이터"),
                fieldWithPath("data[].id").description("사용자 ID"),
                fieldWithPath("data[].account").description("계정"),
                fieldWithPath("data[].name").description("이름"),
                fieldWithPath("data[].age").description("나이").type(JsonFieldType.NUMBER).optional(),
                fieldWithPath("data[].type").description("사용자 유형"),
                fieldWithPath("data[].createdAt").description("가입일")
        );
    }
}
