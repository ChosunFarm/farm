package farm.farmshop.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AuctionProductController {


    // 상품 등록 페이지 이동
    @GetMapping("/register-product")
    public String registerProductForm(Model model, HttpSession session) {
        // 로그인 여부 체크
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");
        model.addAttribute("isLogin", true);
        model.addAttribute("username", username);

        return "product/registerForm";
    }
}