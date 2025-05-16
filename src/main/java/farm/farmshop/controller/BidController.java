package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.Bid;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.BidRepository;
import farm.farmshop.service.ProductService;
import farm.farmshop.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BidController {

    private final ProductService productService;
    private final BidService bidService;
    private final MemberRepository memberRepository;

    // 상품 상세 페이지 및 입찰 페이지
    @GetMapping("/auction/detail/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model, Principal principal) {
        // 로그인 정보 추가
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                model.addAttribute("username", member.getUsername());
                model.addAttribute("memberId", member.getId());
                model.addAttribute("isLogin", true);
            }
        } else {
            model.addAttribute("isLogin", false);
        }

        // 상품 정보 조회
        Product product = productService.findOne(productId);
        if (product == null) {
            return "redirect:/live_auction";
        }

        // 입찰 내역 조회
        List<Bid> bids = bidService.findByProductId(productId);

        model.addAttribute("product", product);
        model.addAttribute("bids", bids);

        // 템플릿 경로를 templates 폴더 기준으로 수정
        return "auction/detail";
    }

    // 입찰 처리(AJAX)
    @PostMapping("/auction/bid")
    @ResponseBody
    public ResponseEntity<?> placeBid(@RequestBody Map<String, Object> bidData, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }

        try {
            Long productId = Long.parseLong(bidData.get("productId").toString());
            Integer bidAmount = Integer.parseInt(bidData.get("bidAmount").toString());

            // 로그인 사용자 정보
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            // 상품 정보 조회
            Product product = productService.findOne(productId);

            // 입찰 유효성 검사
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "상품을 찾을 수 없습니다."));
            }

            // 현재 최고 입찰가보다 높아야 함
            if (product.getCurrentBidPrice() != null && bidAmount <= product.getCurrentBidPrice()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "현재 입찰가보다 높은 금액을 제시해야 합니다."));
            }

            // 시작가보다 높아야 함
            if (bidAmount < product.getPrice()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "시작가보다 높은 금액을 제시해야 합니다."));
            }

            // 입찰 정보 저장
            Bid bid = new Bid();
            bid.setProduct(product);
            bid.setMember(member);
            bid.setBidAmount(bidAmount);
            bid.setBidTime(LocalDateTime.now());
            bid.setStatus("pending"); // 대기 상태

            bidService.saveBid(bid);

            // 상품 정보 업데이트
            product.setCurrentBidPrice(bidAmount);
            product.setBidCount(product.getBidCount() + 1);
            productService.saveProduct(product);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "입찰이 성공적으로 처리되었습니다.",
                    "currentPrice", bidAmount,
                    "bidCount", product.getBidCount())
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "입찰 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 판매자의 입찰 수락 처리
    @PostMapping("/auction/accept-bid")
    public String acceptBid(@RequestParam("bidId") Long bidId, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }

        try {
            // 로그인 사용자 정보
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            // 입찰 정보 조회
            Bid bid = bidService.findById(bidId);
            if (bid == null) {
                redirectAttributes.addFlashAttribute("message", "입찰 정보를 찾을 수 없습니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/mypage";
            }

            // 상품 정보 조회
            Product product = bid.getProduct();

            // 판매자 확인
            if (!product.getMember().getId().equals(member.getId())) {
                redirectAttributes.addFlashAttribute("message", "본인의 상품에 대한 입찰만 수락할 수 있습니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/mypage";
            }

            // 입찰 수락 처리
            bidService.acceptBid(bid);

            // 상품 상태 업데이트
            product.setStatus("completed"); // 판매 완료 상태로 변경
            productService.saveProduct(product);

            redirectAttributes.addFlashAttribute("message", "입찰이 성공적으로 수락되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/mypage/received-bids"; // 수락된 입찰 목록 페이지로 리다이렉트

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "입찰 수락 처리 중 오류가 발생했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/mypage/received-bids";
        }
    }
}