package com.logus.blog.repository;


import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
//@RequiredArgsConstructor
public interface MemberRepository extends JpaRepository<Member, Long> {

//    private final EntityManager em;
//
//    public void save(Member member) {
//        em.persist(member);
//    }
//
//    public Member findOne(Long id) {
//        return em.find(Member.class, id); //Member 찾아서 반환
//    }
//
//    public List<Member> findAll() {
//        return em.createQuery("select m from Member m", Member.class) // (jpql, 반환타입)
//                .getResultList();
//    }
//
//    public List<Member> findByNickname(String nickname) {
//        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
//                .setParameter("nickname", nickname)
//                .getResultList();
//    }
}
