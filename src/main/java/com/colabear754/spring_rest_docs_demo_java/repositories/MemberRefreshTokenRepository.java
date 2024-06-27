package com.colabear754.spring_rest_docs_demo_java.repositories;

import com.colabear754.spring_rest_docs_demo_java.entity.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, UUID> {
    Optional<MemberRefreshToken> findByMemberIdAndReissueCountLessThan(UUID id, long count);
}
