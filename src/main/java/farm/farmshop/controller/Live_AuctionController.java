package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class Live_AuctionController {
    
    private final MemberRepository memberRepository;
    @GetMapping("/live_auction")
    public String showLiveAuctionPage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName(); // 로그인한 사용자의 이메일
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } 
        else {
            model.addAttribute("isLogin", false);
        }
        return "live_auction"; // templates/live_auction.html 을 렌더링
    }
}
