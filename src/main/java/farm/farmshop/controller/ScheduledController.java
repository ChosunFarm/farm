package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.ProductImage;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ScheduledController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository; // 상품 이미지 레포지토리 추가
    private final ProductService productService;

    @RequestMapping("/scheduled")
    public String main(Model model, Principal principal) {
        // 로그인 관련 코드
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

        // 검수 대기 상품 및 미검수 상품 가져오기
        List<Product> pendingProducts = productService.findByStatus("pending");
        List<Product> unprocessedProducts = productService.findByStatusNull();

        // 검수 대기 상품과 미검수 상품을 함께 표시
        model.addAttribute("pendingProducts", pendingProducts);
        model.addAttribute("unprocessedProducts", unprocessedProducts);

        return "scheduled"; // templates/scheduled.html
    }
}