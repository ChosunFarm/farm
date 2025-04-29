package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @GetMapping("")
    public String adminMain(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 회원 목록 가져오기
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        // 상품 목록 가져오기
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        return "admin/dashboard";
    }

    @GetMapping("/members")
    public String memberManagement(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 회원 목록 가져오기
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        return "admin/members";
    }

    @GetMapping("/products")
    public String productManagement(Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 상품 목록 가져오기
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        return "admin/products";
    }

    @PostMapping("/member/update-role")
    public String updateMemberRole(@RequestParam("memberId") Long memberId,
                                   @RequestParam("role") String role) {
        Member member = memberRepository.findOne(memberId);
        if (member != null) {
            member.setUser_type(role);
            memberRepository.save(member);
        }
        return "redirect:/admin/members";
    }
}