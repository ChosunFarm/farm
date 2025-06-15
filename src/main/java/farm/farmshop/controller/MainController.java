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
                model.addAttribute("profileImage", member.getProfileImage());
                model.addAttribute("isLogin", true);
                model.addAttribute("memberId", member.getId());
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 검수 상태에 따라 상품 분리 조회
        List<Product> approvedProducts = productRepository.findByStatus("approved");
        List<Product> pendingProducts = productRepository.findByStatus("pending");
        List<Product> completedProducts = productRepository.findByStatus("completed");

        List<Product> allProducts = Stream.of(
                approvedProducts,
                pendingProducts,
                completedProducts
        ).flatMap(List::stream).collect(Collectors.toList());

        List<Long> productIds = allProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<Long, List<String>> productImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        pi -> pi.getProduct().getId(),
                        Collectors.mapping(ProductImage::getImageUrl, Collectors.toList())
                ));


        for (Product product : allProducts) {
            List<String> imageUrls = productImageMap.get(product.getId());
            String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
            product.setImageUrl(imageUrl);
        }

        // 모델에 검수 상태에 따른 분리된 리스트 전달
        model.addAttribute("approvedProducts", approvedProducts); // 실시간 경매 상품
        model.addAttribute("pendingProducts", pendingProducts);   // 신규 경매 예정 상품
        model.addAttribute("completedProducts", completedProducts);   // 신규 경매 예정 상품
        model.addAttribute("productImageMap", productImageMap);


        return "main";
    }
}