package farm.farmshop.controller;

import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
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
    private final ProductRepository productRepository; // 추가

    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);

                // 사용자의 상품 목록 조회
                List<Product> products = productRepository.findByMember(member);

                // DTO로 변환
                List<MyProductDTO> productDTOs = new ArrayList<>();
                for (Product product : products) {
                    MyProductDTO dto = new MyProductDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setImageUrl(product.getImageUrl());

                    // 상품 타입과 무게 정보 설정
                    if ("F".equals(product.getDtype())) {
                        Fruit fruit = (Fruit) product;
                        dto.setProductType("과일");
                        dto.setWeightInfo((fruit.getGram() / 1000.0) + "kg");
                    } else if ("V".equals(product.getDtype())) {
                        Vegetable veg = (Vegetable) product;
                        dto.setProductType("채소");
                        dto.setWeightInfo((veg.getGram() / 1000.0) + "kg");
                    } else if ("G".equals(product.getDtype())) {
                        Grain grain = (Grain) product;
                        dto.setProductType("곡물");
                        dto.setWeightInfo((grain.getGram() / 1000.0) + "kg");
                    }

                    // 상품 상태 설정
                    String status = product.getStatus();
                    if (status == null) {
                        dto.setStatus("검수 신청 전");
                        dto.setStatusClass("waiting");
                    } else if ("pending".equals(status)) {
                        dto.setStatus("검수 대기중");
                        dto.setStatusClass("pending");
                    } else if ("approved".equals(status)) {
                        dto.setStatus("경매 진행중");
                        dto.setStatusClass("active");
                    } else if ("rejected".equals(status)) {
                        dto.setStatus("검수 거부됨");
                        dto.setStatusClass("rejected");
                    } else if ("completed".equals(status)) {
                        dto.setStatus("판매 완료");
                        dto.setStatusClass("success");
                    } else {
                        dto.setStatus("상태 미확인");
                        dto.setStatusClass("waiting");
                    }

                    productDTOs.add(dto);
                }

                model.addAttribute("productDTOs", productDTOs);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        return "mypage";
    }
}