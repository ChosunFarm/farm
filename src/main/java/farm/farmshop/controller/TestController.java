package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final ProductService productService;

    @GetMapping("/test")
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

        // 낙찰 상품만 가져오기
        List<Product> completedProducts = productService.findByStatus("completed");

        // 검색어가 있는 경우 필터링
        if (search != null && !search.isEmpty()) {
            completedProducts = completedProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        // 카테고리 필터링이 있는 경우
        if (category != null && !category.isEmpty()) {
            completedProducts = completedProducts.stream()
                    .filter(p -> p.getDtype().equals(category))
                    .toList();
        }

        model.addAttribute("completedProducts", completedProducts);
        model.addAttribute("search", search);
        model.addAttribute("category", category);

        return "test"; // templates/live_auction.html 을 렌더링
    }

    // 특정 카테고리별 상품 목록 페이지
    @GetMapping("/test/category/{category}")
    public String showCategoryProducts(@PathVariable String category, Model model, Principal principal) {
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

        // 카테고리별로 승인된 상품 조회
        List<Product> categoryProducts = productService.findByCategoryAndStatus(category, "completed");
        model.addAttribute("completedProducts", categoryProducts);
        model.addAttribute("category", category);

        // 카테고리명 설정
        String categoryName;
        switch(category) {
            case "F":
                categoryName = "과일";
                break;
            case "V":
                categoryName = "채소";
                break;
            case "G":
                categoryName = "곡물";
                break;
            default:
                categoryName = "전체 상품";
        }
        model.addAttribute("categoryName", categoryName);

        return "test";
    }
}