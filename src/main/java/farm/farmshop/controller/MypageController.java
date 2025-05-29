package farm.farmshop.controller;

import farm.farmshop.dto.MyBidDTO;
import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;

    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);

                } 
            else {
            model.addAttribute("isLogin", false);
            }

        }
        return "mypage";
    }
}