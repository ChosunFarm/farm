package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.AuctionResult;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.AuctionResultRepository;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PagedetailController {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final AuctionResultRepository auctionResultRepository;
    private final AlertService alertService;
    
    @GetMapping("/mypage/pagedetail/{username}")
    public String pageDetail(
            @PathVariable("username") String username,
            Model model,
            Principal principal
    ) {
        // 로그인 상태 처리
        if (principal != null) {
            Member loginMember = memberRepository.findByEmail(principal.getName());
            model.addAttribute("isLogin", loginMember != null);
            if (loginMember != null) {
                model.addAttribute("username", loginMember.getUsername());

                Long memberId = loginMember.getId();
                model.addAttribute("memberId", memberId);

                long unreadCnt = alertService.countUnread(memberId);
                model.addAttribute("alertCount", unreadCnt);

                List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                                    .stream()
                                                    .map(AlertDto::fromEntity)
                                                    .toList();
                model.addAttribute("alertList", unreadList);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 판매자(프로필 주인) 조회
        Member seller = memberRepository.findByUsername(username);
        if (seller == null) {
            return "redirect:/";
        }
        model.addAttribute("seller", seller);

        String fullAddress = seller.getAddress();        // Member 엔티티에 주소 필드가 있다고 가정
        String shortAddress = trimAddress(fullAddress);   // 앞 세 단어까지만 잘라 주는 메소드
        model.addAttribute("address", shortAddress);

        // 판매 상품 목록
        List<Product> sellerProducts = productRepository.findByMember(seller);
        model.addAttribute("sellerProducts", sellerProducts);

        // 상품 상태 DTO
        List<MyProductDTO> productDTOs = new ArrayList<>();
        for (Product product : sellerProducts) {
            MyProductDTO dto = new MyProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setImageUrl(product.getImageUrl());
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

        // 완료된 거래 (낙찰자 또는 판매자로 참여한 후 deliveryStatus = COMPLETED)
        List<AuctionResult> completedTransactions =
            auctionResultRepository.findCompletedByMemberId(seller.getId());
        model.addAttribute("completedTransactions", completedTransactions);

        // 5) 거래 후기(리뷰가 있는 AuctionResult) 목록 → 뷰에서 `${reviewedResults}` 사용
        List<AuctionResult> reviewedResults =
            auctionResultRepository.findBySellerIdAndReviewIsNotNull(seller.getId());
        model.addAttribute("reviewedResults", reviewedResults);
        
        List<AuctionResult> ratingsList =
            auctionResultRepository.findByProduct_Member_IdAndRatingIsNotNull(seller.getId());
        model.addAttribute("ratingsList", ratingsList);

        // 평균 평점 (AVG(rating))
        Double avg = auctionResultRepository.findAvgRatingBySellerId(seller.getId());
        double avgRating = (avg != null) ? avg : 0.0;
        model.addAttribute("avgRating", avgRating);
        
        return "mypage/pagedetail";
    }
        private String trimAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isBlank()) return "";

        String[] parts = fullAddress.trim().split("\\s+");
        int end = Math.min(parts.length, 3); // 앞 세 단어까지만
        return String.join(" ", Arrays.copyOfRange(parts, 0, end));
    }
}
