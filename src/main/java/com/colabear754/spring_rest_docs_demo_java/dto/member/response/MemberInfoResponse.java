package com.colabear754.spring_rest_docs_demo_java.dto.member.response;

import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberInfoResponse(
        UUID id,
        String account,
        String name,
        Integer age,
        MemberType type,
        LocalDateTime createdAt
) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getAccount(),
                member.getName(),
                member.getAge(),
                member.getType(),
                member.getCreatedAt()
        );
    }
}
