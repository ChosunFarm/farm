package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.ProductImage;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @RequestMapping("/")
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

        // 검수 상태에 따라 상품 분리 조회
        List<Product> approvedProducts = productRepository.findByStatus("approved");
        List<Product> pendingProducts = productRepository.findByStatus("pending");

        // approved + pending 상품 전체에 이미지 매핑 적용
        List<Product> allProducts = Stream.concat(
            approvedProducts.stream(),
            pendingProducts.stream()
        ).collect(Collectors.toList());

        List<Long> productIds = allProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<Long, String> productImageMap = productImages.stream()
                .collect(Collectors.toMap(
                        ProductImage::getProductId,
                        ProductImage::getImageUrl,
                        (existing, replacement) -> existing
                ));

        for (Product product : allProducts) {
            String imageUrl = productImageMap.get(product.getId());
            product.setImageUrl(imageUrl);
        }

        // 모델에 검수 상태에 따른 분리된 리스트 전달
        model.addAttribute("approvedProducts", approvedProducts); // 실시간 경매 상품
        model.addAttribute("pendingProducts", pendingProducts);   // 신규 경매 예정 상품

        return "main";
    }
}