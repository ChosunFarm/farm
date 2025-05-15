package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("mypage/my-products")
public class MyProductsController {
    private final MemberRepository memberRepository;

    @GetMapping
    public String proposePage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName(); // 로그인한 사용자의 이메일
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        return "mypage/my-products"; // templates/propose.html
    }
}


