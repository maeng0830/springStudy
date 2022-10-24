package maeng0830.hellospring.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import maeng0830.hellospring.domain.Member;

public class JpaMemberRepository implements MemberRepository {

    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        // pk 기반이 아닌 쿼리는 createQuery를 통해 작성해야한다.
        List<Member> result = em.createQuery("select m from Member m where m.name = :name",
                Member.class)
            .setParameter("name", name)
            .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        List<Member> result = em.createQuery("select m from Member m", Member.class)
            .getResultList();

        return result;
    }
}
