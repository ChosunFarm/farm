package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.dto.MyBidDTO;
import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.ProductImage;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.AlertService;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class MyProductsController {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;
    private final AlertService alertService;
    private final ProductImageRepository productImageRepository;

    @GetMapping("mypage/my-products")
    public String proposePage(Model model, Principal principal) {
        List<Product> products = new ArrayList<>();
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
                
                // 사용자의 상품 목록 조회
                products = productRepository.findByMember(member);

                // DTO로 변환
                List<MyProductDTO> productDTOs = new ArrayList<>();
                for (Product product : products) {
                    MyProductDTO dto = new MyProductDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setImageUrl(product.getImageUrl());

                    // 상품 타입과 무게 정보 설정 - 이 부분을 수정
                    String dtype = product.getDtype();
                    if ("F".equals(dtype)) {
                        // 타입 체크로 instanceof 사용 대신 dtype 확인
                        dto.setProductType("과일");
                        // 직접 조회로 처리
                        dto.setWeightInfo((product.getGram() / 1000.0) + "kg");
                    } else if ("V".equals(dtype)) {
                        dto.setProductType("채소");
                        dto.setWeightInfo((product.getGram() / 1000.0) + "kg");
                    } else if ("G".equals(dtype)) {
                        dto.setProductType("곡물");
                        dto.setWeightInfo((product.getGram() / 1000.0) + "kg");
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

        List<Long> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<Long, List<String>> productImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                    pi -> pi.getProduct().getId(),
                    Collectors.mapping(ProductImage::getImageUrl, Collectors.toList())
                ));

                model.addAttribute("productImageMap", productImageMap);

        return "mypage/my-products"; // templates/propose.html
    }
}