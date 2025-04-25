package farm.farmshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MypageController {
    
    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // 로그인한 사용자 아이디
            model.addAttribute("username", username);

            // 여기서 사용자 추가 정보도 서비스 통해 가져올 수 있음 (예: 이름, 주소 등)
            // model.addAttribute("userInfo", userService.findByUsername(username));

            return "mypage"; // templates/mypage.html
        }

        return "redirect:/login"; // 비로그인 접근 방지
    }

}
