package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public String signup(@ModelAttribute Member member) {
        memberService.join(member);
        return "redirect:/login"; // 회원가입 후 로그인 페이지로 리디렉션
    }
}
