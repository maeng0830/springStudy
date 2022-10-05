package maeng0830.hellospring.repository;

import maeng0830.hellospring.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member); // 저장소에 Member 객체 저장
    Optional<Member> findById(Long id); // id로 해당하는 Member 객체 찾기
    Optional<Member> findByName(String name); // name으로 해당하는 Member 객체 찾기
    List<Member> findAll(); // 저장소에 저장된 모든 Member 객체 불러오기
}
