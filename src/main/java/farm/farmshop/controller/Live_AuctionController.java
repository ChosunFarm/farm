package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.ProductImage;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.service.AlertService;
import farm.farmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class Live_AuctionController {

    private final MemberRepository memberRepository;
    private final AlertService alertService;
    private final ProductService productService;
    private final ProductImageRepository productImageRepository;


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

                Long memberId = member.getId();
                model.addAttribute("memberId", memberId);

                long unreadCnt = alertService.countUnread(memberId);
                model.addAttribute("alertCount", unreadCnt);

                List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                        .stream()
                        .map(AlertDto::fromEntity)
                        .toList();
                model.addAttribute("alertList", unreadList);

            }
        }
        else {
            model.addAttribute("isLogin", false);
        }

        // 검수 승인된 상품(status가 'approved'인 상품)만 가져오기
        List<Product> approvedProducts = productService.findByStatus("approved");

        // 검색어가 있는 경우 필터링
        if (search != null && !search.isEmpty()) {
            approvedProducts = approvedProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        // 카테고리 필터링이 있는 경우
        if (category != null && !category.isEmpty()) {
            approvedProducts = approvedProducts.stream()
                    .filter(p -> p.getDtype().equals(category))
                    .toList();
        }

        List<Long> productIds = approvedProducts.stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);

        Map<Long, List<String>> productImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.mapping(ProductImage::getImageUrl, Collectors.toList())
                ));

        model.addAttribute("approvedProducts", approvedProducts);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("productImageMap", productImageMap);

        return "live_auction"; // templates/live_auction.html 을 렌더링
    }

    // 특정 카테고리별 상품 목록 페이지
    @GetMapping("/live_auction/category/{category}")
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
        List<Product> categoryProducts = productService.findByCategoryAndStatus(category, "approved");

        // 이미지 매핑 추가
        List<Long> productIds = categoryProducts.stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<Long, List<String>> productImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.mapping(ProductImage::getImageUrl, Collectors.toList())
                ));

        model.addAttribute("approvedProducts", categoryProducts);
        model.addAttribute("category", category);
        model.addAttribute("productImageMap", productImageMap);

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

        return "live_auction";
    }
}