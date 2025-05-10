package farm.farmshop.service;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(Member member) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

        // 생성 시간 설정
        member.setCreated_at(LocalDateTime.now());

        // 기본 사용자 타입 설정 (필요한 경우)
        if (member.getUser_type() == null) {
            member.setUser_type("USER");
        }

        memberRepository.save(member);
    }

    @Transactional
    public void createAdmin(String email, String password, String username) {
        Member admin = new Member();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setUsername(username);
        admin.setUser_type("ADMIN");
        admin.setCreated_at(LocalDateTime.now());

        memberRepository.save(admin);
    }

    // 스프링 시큐리티가 사용자를 인증하기 위해 사용하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username 파라미터가 실제로는 email 값임
        Member member = memberRepository.findByEmail(username);

        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 사용자 권한 설정 - user_type 필드 값 사용
        String role = "ROLE_" + member.getUser_type();

        return new User(
                member.getEmail(),  // 로그인 ID로 사용할 값
                member.getPassword(), // 암호화된 비밀번호
                Collections.singletonList(new SimpleGrantedAuthority(role)) // 권한 목록
        );
    }
}