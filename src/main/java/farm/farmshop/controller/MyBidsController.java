package farm.farmshop.controller;

import farm.farmshop.dto.MyBidDTO;
import farm.farmshop.dto.MyProductDTO;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Fruit;
import farm.farmshop.entity.product.Grain;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.product.Vegetable;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class MyBidsController {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;

    @GetMapping("mypage/mybids")
    public String proposePage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName(); // 로그인한 사용자의 이메일
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("isLogin", true);
            }
            // 사용자의 입찰 목록 조회
                List<Bid> myBids = bidService.findByMemberId(member.getId());
                List<MyBidDTO> bidDTOs = new ArrayList<>();

                for (Bid bid : myBids) {
                    MyBidDTO dto = new MyBidDTO();
                    dto.setId(bid.getId());
                    dto.setBidAmount(bid.getBidAmount());
                    dto.setBidTime(bid.getBidTime());
                    dto.setStatus(bid.getStatus());

                    // 상태에 따른 CSS 클래스 설정
                    if ("pending".equals(bid.getStatus())) {
                        dto.setStatusClass("pending");
                        dto.setStatusText("대기중");
                    } else if ("accepted".equals(bid.getStatus())) {
                        dto.setStatusClass("success");
                        dto.setStatusText("수락됨");
                    } else {
                        dto.setStatusClass("rejected");
                        dto.setStatusText("거부됨");
                    }

                    // 상품 정보 설정
                    Product product = bid.getProduct();
                    if (product != null) {
                        dto.setProductId(product.getId());
                        dto.setProductName(product.getName());
                        dto.setImageUrl(product.getImageUrl());

                        // 무게 정보 설정 - 직접 조회 방식으로 변경
                        dto.setWeightInfo((product.getGram() / 1000.0) + "kg");
                    }

                    bidDTOs.add(dto);
                }

                model.addAttribute("bidDTOs", bidDTOs);
        } else {
            model.addAttribute("isLogin", false);
        }

        return "mypage/mybids"; // templates/propose.html
    }
}


