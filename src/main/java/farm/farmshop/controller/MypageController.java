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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BidService bidService;

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

                // 내 상품에 대한 입찰 목록 조회 (판매자용)
                List<Bid> receivedBids = bidService.findByProductSellerId(member.getId());
                List<MyBidDTO> receivedBidDTOs = new ArrayList<>();

                for (Bid bid : receivedBids) {
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

                    // 입찰자 정보 설정
                    Member bidder = bid.getMember();
                    if (bidder != null) {
                        dto.setBidderName(bidder.getUsername());
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

                    receivedBidDTOs.add(dto);
                }

                model.addAttribute("receivedBidDTOs", receivedBidDTOs);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        return "mypage";
    }
}