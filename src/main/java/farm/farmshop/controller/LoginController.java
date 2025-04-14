package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }
    /*private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Member loginMember = memberService.login(email, password);
        if (loginMember == null) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 틀렸습니다.");
            return "redirect:/login"; // 실패 시 다시 로그인 화면
        }

        session.setAttribute("loginMember", loginMember);
        return "redirect:/"; // 성공 시 메인 화면으로
    }*/
}

