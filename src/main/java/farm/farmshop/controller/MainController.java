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

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository; // 상품 이미지 레포지토리 추가

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

        // 상품 목록 가져오기
        List<Product> products = productRepository.findAll();

        // 상품 이미지 URL 가져오기
        // 각 상품 ID에 대한 첫 번째 이미지를 찾아서 맵으로 저장
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());


        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<Long, String> productImageMap = productImages.stream()
                .collect(Collectors.toMap(
                        ProductImage::getProductId,
                        ProductImage::getImageUrl,
                        (existing, replacement) -> existing // 중복된 경우 첫 번째 값 유지
                ));

        // 각 상품에 이미지 URL 설정
        for (Product product : products) {
            String imageUrl = productImageMap.get(product.getId());
            product.setImageUrl(imageUrl); // Product 클래스에 imageUrl 필드가 있어야 함
        }

        model.addAttribute("products", products);

        return "main";



    }
}