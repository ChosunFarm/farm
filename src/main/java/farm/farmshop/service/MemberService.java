package farm.farmshop.service;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void join(Member member) {
        memberRepository.save(member);
    }

    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getPassword().equals(password)) {
            return member; // 로그인 성공
        }
        return null; // 실패
    }
}
