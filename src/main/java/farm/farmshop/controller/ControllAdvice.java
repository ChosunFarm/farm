package farm.farmshop.config;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllAdvice {

    private final MemberRepository memberRepository;

    @ModelAttribute("profileImage")
    public String addProfileImage(Principal principal) {
        if (principal == null) return null;

        Member member = memberRepository.findByEmail(principal.getName());
        if (member != null) {
            return member.getProfileImage();
        }
        return null;
    }

    @ModelAttribute("username")
    public String addUsername(Principal principal) {
        if (principal == null) return null;

        Member member = memberRepository.findByEmail(principal.getName());
        if (member != null) {
            return member.getUsername();
        }
        return null;
    }

    @ModelAttribute("isLogin")
    public boolean isLogin(Principal principal) {
        return principal != null;
    }
}
