package com.colabear754.spring_rest_docs_demo_java.dto.member.response;

import com.colabear754.spring_rest_docs_demo_java.entity.Member;

public record MemberUpdateResponse(
        boolean result,
        String name,
        Integer age
) {
    public static MemberUpdateResponse of(boolean result, Member member) {
        return new MemberUpdateResponse(result, member.getName(), member.getAge());
    }
}
