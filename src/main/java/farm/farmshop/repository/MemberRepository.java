package farm.farmshop.repository;

import farm.farmshop.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;
    // 스프링이 EntityManger를 만들어서 의존성 주입을 해준다.

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name",name)
                .getResultList();
    }
    public Member findByEmail(String email) {
        List<Member> result = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Member> findByUsernameOrEmailContaining(String keyword) {
        try {
            return em.createQuery(
                            "SELECT m FROM Member m WHERE m.username LIKE :keyword OR m.email LIKE :keyword",
                            Member.class
                    )
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        } catch (Exception e) {
            // 예외 로깅
            e.printStackTrace();
            // 예외 발생 시 빈 리스트 반환
            return List.of();
        }
    }

}
