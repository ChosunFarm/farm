package farm.farmshop.controller;

import farm.farmshop.dto.BidUpdateDTO;
import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.Bid;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.service.ProductService;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RealtimeBidController {

    private final ProductService productService;
    private final BidService bidService;
    private final MemberRepository memberRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // WebSocket을 통한 입찰 처리
    @MessageMapping("/bid")
    public void placeBid(@Payload Map<String, Object> bidData, Principal principal) {
        try {
            if (principal == null || messagingTemplate == null) {
                return;
            }

            Long productId = Long.parseLong(bidData.get("productId").toString());
            Integer bidAmount = Integer.parseInt(bidData.get("bidAmount").toString());

            // 로그인 사용자 정보
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            if (member == null) {
                return;
            }

            // 상품 정보 조회
            Product product = productService.findOne(productId);
            if (product == null) {
                return;
            }

            // 입찰 유효성 검사
            if (!isValidBid(product, member, bidAmount)) {
                return;
            }

            // 입찰 정보 저장
            Bid bid = new Bid();
            bid.setProduct(product);
            bid.setMember(member);
            bid.setBidAmount(bidAmount);
            bid.setBidTime(LocalDateTime.now());
            bid.setStatus("pending");

            bidService.saveBid(bid);

            // 상품 정보 업데이트
            product.setCurrentBidPrice(bidAmount);
            product.setBidCount(product.getBidCount() + 1);
            productService.saveProduct(product);

            // 실시간으로 모든 클라이언트에게 입찰 정보 전송
            BidUpdateDTO bidUpdate = new BidUpdateDTO(
                    productId,
                    member.getUsername(),
                    bidAmount,
                    LocalDateTime.now(),
                    product.getBidCount(),
                    "success",
                    member.getUsername() + "님이 " + String.format("%,d", bidAmount) + "원에 입찰했습니다."
            );

            // 특정 상품을 구독하고 있는 모든 클라이언트에게 전송
            messagingTemplate.convertAndSend("/topic/auction/" + productId, bidUpdate);

        } catch (Exception e) {
            // 에러 발생 시 에러 메시지 전송
            if (messagingTemplate != null) {
                BidUpdateDTO errorUpdate = new BidUpdateDTO();
                errorUpdate.setStatus("error");
                errorUpdate.setMessage("입찰 처리 중 오류가 발생했습니다.");

                String productId = bidData.get("productId").toString();
                messagingTemplate.convertAndSend("/topic/auction/" + productId, errorUpdate);
            }
        }
    }

    // 입찰 유효성 검사
    private boolean isValidBid(Product product, Member member, Integer bidAmount) {
        // 자신의 상품에는 입찰할 수 없음
        if (product.getMember().getId().equals(member.getId())) {
            return false;
        }

        // 이미 완료된 경매인지 확인
        if ("completed".equals(product.getStatus())) {
            return false;
        }

        // 현재 최고 입찰가보다 높아야 함
        if (product.getCurrentBidPrice() != null && bidAmount <= product.getCurrentBidPrice()) {
            return false;
        }

        // 시작가보다 높아야 함
        if (bidAmount < product.getPrice()) {
            return false;
        }

        // 100원 단위 체크
        return bidAmount % 100 == 0;
    }

    // 경매 상품 정보 실시간 업데이트
    @MessageMapping("/auction/join")
    public void joinAuction(@Payload Map<String, Object> data, Principal principal) {
        try {
            if (messagingTemplate == null) {
                return;
            }

            Long productId = Long.parseLong(data.get("productId").toString());
            Product product = productService.findOne(productId);

            if (product != null) {
                BidUpdateDTO joinUpdate = new BidUpdateDTO();
                joinUpdate.setProductId(productId);
                joinUpdate.setBidAmount(product.getCurrentBidPrice());
                joinUpdate.setBidCount(product.getBidCount());
                joinUpdate.setStatus("joined");
                joinUpdate.setMessage("경매에 참여했습니다.");

                // 특정 사용자에게만 현재 상태 전송
                if (principal != null) {
                    messagingTemplate.convertAndSendToUser(
                            principal.getName(),
                            "/queue/auction/" + productId,
                            joinUpdate
                    );
                }
            }
        } catch (Exception e) {
            // 에러 처리
        }
    }
}