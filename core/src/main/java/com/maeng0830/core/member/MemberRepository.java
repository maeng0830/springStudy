package com.maeng0830.core.member;

public interface MemberRepository {
    void save(Member member);

    Member findById(Long memberId);
}
