package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class Live_AuctionController {

    private final MemberRepository memberRepository;
    private final ProductService productService;

    @GetMapping("/live_auction")
    public String showLiveAuctionPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Model model,
            Principal principal) {
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

        // 검수 승인된 상품(status가 'approved'인 상품)만 가져오기
        List<Product> approvedProducts = productService.findByStatus("approved");
        model.addAttribute("approvedProducts", approvedProducts);

        return "live_auction"; // templates/live_auction.html 을 렌더링
    }
}