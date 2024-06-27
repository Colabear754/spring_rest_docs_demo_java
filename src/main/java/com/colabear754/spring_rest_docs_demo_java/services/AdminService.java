package com.colabear754.spring_rest_docs_demo_java.services;

import com.colabear754.spring_rest_docs_demo_java.common.MemberType;
import com.colabear754.spring_rest_docs_demo_java.dto.member.response.MemberInfoResponse;
import com.colabear754.spring_rest_docs_demo_java.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MemberInfoResponse> getMembers() {
        return memberRepository.findAllByType(MemberType.USER).stream()
                .map(MemberInfoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberInfoResponse> getAdmins() {
        return memberRepository.findAllByType(MemberType.ADMIN).stream()
                .map(MemberInfoResponse::from)
                .toList();
    }
}
