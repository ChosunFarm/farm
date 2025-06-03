package farm.farmshop.service;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.dto.MemberEditDTO;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(Member member) {
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        member.setCreated_at(LocalDateTime.now());

        if (member.getUser_type() == null) {
            member.setUser_type("USER");
        }

        memberRepository.save(member);
    }

    @Transactional
    public void createAdmin(String email, String password, String username) {
        Member admin = new Member();
        admin.setEmail(email.toLowerCase().trim()); // 이메일 정리
        admin.setPassword(passwordEncoder.encode(password));
        admin.setUsername(username);
        admin.setUser_type("ADMIN");
        admin.setCreated_at(LocalDateTime.now());

        memberRepository.save(admin);
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //     String processedEmail = username.trim().toLowerCase();

    //     Member member = memberRepository.findByEmail(processedEmail);

    //     if (member == null) {
    //         throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + processedEmail);
    //     }

    //     String role = "ROLE_" + member.getUser_type();

    //     return new User(
    //             member.getEmail(),
    //             member.getPassword(),
    //             Collections.singletonList(new SimpleGrantedAuthority(role))
    //     );
    // }

    @Transactional
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public boolean updateMemberInfo(String email, MemberEditDTO dto) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            member.setUsername(dto.getUsername());
            member.setPhone(dto.getPhone());
            member.setAddress(dto.getAddress());
            return true;
        }
        return false;
    }
}
