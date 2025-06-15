package farm.farmshop.controller;

import farm.farmshop.entity.Member;
import farm.farmshop.dto.AlertDto;
import farm.farmshop.entity.product.Product;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.AuctionResult;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.repository.BidRepository;
import farm.farmshop.service.AlertService;
import farm.farmshop.service.ProductService;
import farm.farmshop.service.BidService;
import farm.farmshop.service.RatingService;
import farm.farmshop.service.AuctionResultService;
import farm.farmshop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import farm.farmshop.repository.ProductImageRepository;
import farm.farmshop.entity.product.ProductImage;



import java.util.stream.Collectors;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*") // 또는 특정 도메인
@Controller
@RequiredArgsConstructor
public class BidController {

    private final ProductService productService;
    private final AlertService alertService;
    private final BidService bidService;
    private final MemberRepository memberRepository;
    private final AuctionResultService auctionResultService;
    private final NotificationService notificationService;
    private final RatingService ratingService;
    private final ProductImageRepository productImageRepository;


    @ModelAttribute
    public void populateAlerts(Model model, Principal principal) {
        if (principal == null) return;

        Member member = memberRepository.findByEmail(principal.getName());
        if (member == null) return;

        Long memberId = member.getId();
        model.addAttribute("memberId", memberId);

        long unreadCnt = alertService.countUnread(memberId);
        model.addAttribute("alertCount", unreadCnt);

        List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                                .stream()
                                                .map(AlertDto::fromEntity)
                                                .toList();
        model.addAttribute("alertList", unreadList);
    }

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
                model.addAttribute("profileImage", member.getProfileImage());
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
        long uniqueBidderCount = bids.stream()
            .map(b -> b.getMember().getId())
            .distinct()
            .count();
        model.addAttribute("uniqueBidderCount", uniqueBidderCount);
        // 이미지 추가
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        List<String> imageUrls = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        Map<Long, List<String>> productImageMap = Map.of(productId, imageUrls);
        model.addAttribute("productImageMap", productImageMap);

        // 판매자 평점
        Member seller = product.getMember();
        List<AuctionResult> ratingsList = auctionResultService.findRatingsBySeller(seller.getId());
        if (ratingsList.isEmpty()) {
            // 리뷰가 하나도 없으면 avgRating을 null로
            model.addAttribute("avgRating", null);
        } else {
            // 리뷰가 있으면 평균을 계산
            double rawAvg = ratingService.getAvgRating(seller.getId());
            double avgRating = Math.round(rawAvg * 2) / 2.0;
            model.addAttribute("avgRating", avgRating);
        }
        List<Product> approvedProducts = productService.findApprovedProducts();
        model.addAttribute("approvedProducts", approvedProducts);

        List<Product> completedProducts = productService.findCompletedProducts();
        model.addAttribute("completedProducts", completedProducts);
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

            // 자신의 상품에는 입찰할 수 없음
            if (product.getMember().getId().equals(member.getId())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "본인의 상품에는 입찰할 수 없습니다."));
            }

            // 이미 완료된 경매인지 확인
            if ("completed".equals(product.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "이미 완료된 경매입니다."));
            }

            // 현재 최고 입찰가보다 높아야 함
            if (product.getCurrentBidPrice() != null && bidAmount <= product.getCurrentBidPrice()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "현재 입찰가보다 높은 금액을 제시해야 합니다."));
            }

            // 시작가보다 높아야 함
            if (bidAmount < product.getPrice()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "시작가보다 높은 금액을 제시해야 합니다."));
            }

            // 100원 단위 체크
            if (bidAmount % 100 != 0) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "100원 단위로 입찰해주세요."));
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

            // 이미 완료된 경매인지 확인
            if ("completed".equals(product.getStatus())) {
                redirectAttributes.addFlashAttribute("message", "이미 완료된 경매입니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/mypage/received-bids";
            }

            // 입찰 수락 처리
            bidService.acceptBid(bid);

            // 상품 상태 업데이트
            product.setStatus("completed");
            productService.saveProduct(product);

            // 낙찰 결과 생성
            AuctionResult auctionResult = createAuctionResult(bid, product);
            auctionResultService.save(auctionResult);

            // 알림 발송 (낙찰자와 판매자에게)
            notificationService.sendWinningNotification(bid, product);

            redirectAttributes.addFlashAttribute("message", "입찰이 성공적으로 수락되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");

            // 낙찰 완료 페이지로 리다이렉트
            return "redirect:/auction/completed/" + auctionResult.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "입찰 수락 처리 중 오류가 발생했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/mypage/received-bids";
        }
    }

    // 낙찰 완료 페이지
    @GetMapping("/auction/completed/{auctionResultId}")
    public String showCompletedAuction(@PathVariable Long auctionResultId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        AuctionResult auctionResult = auctionResultService.findById(auctionResultId);
        if (auctionResult == null) {
            return "redirect:/";
        }

        // 로그인 사용자 정보
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        model.addAttribute("profileImage", member.getProfileImage());

        // 접근 권한 확인 (낙찰자 또는 판매자만)
        boolean isWinner = auctionResult.getWinningBid().getMember().getId().equals(member.getId());
        boolean isSeller = auctionResult.getProduct().getMember().getId().equals(member.getId());

        if (!isWinner && !isSeller) {
            return "redirect:/";
        }

        Product product = auctionResult.getProduct();
        model.addAttribute("product", product);

        List<ProductImage> imgs = productImageRepository.findByProductId(product.getId());
        List<String> urls = imgs.stream()
                                .map(ProductImage::getImageUrl)
                                .toList();
        model.addAttribute("productImageMap", Map.of(product.getId(), urls));


        model.addAttribute("auctionResult", auctionResult);
        model.addAttribute("isWinner", isWinner);
        model.addAttribute("isSeller", isSeller);
        model.addAttribute("username", member.getUsername());
        model.addAttribute("isLogin", true);

        return "auction/completed";
    }

    // 거래 방법 선택 처리
    @PostMapping("/auction/select-transaction-method")
    public String selectTransactionMethod(
            @RequestParam Long auctionResultId,
            @RequestParam String transactionMethod,
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String deliveryNote,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String tradeNote,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            AuctionResult auctionResult = auctionResultService.findById(auctionResultId);
            if (auctionResult == null) {
                redirectAttributes.addFlashAttribute("message", "경매 결과를 찾을 수 없습니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/";
            }

            // 로그인 사용자 확인
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            // 낙찰자만 거래 방법을 선택할 수 있음
            if (!auctionResult.getWinningBid().getMember().getId().equals(member.getId())) {
                redirectAttributes.addFlashAttribute("message", "접근 권한이 없습니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/";
            }

            // 거래 방법 업데이트
            auctionResult.setTransactionMethod(transactionMethod);

            // 거래 방법별 정보 설정
            switch (transactionMethod) {
                case "DELIVERY":
                    auctionResult.setDeliveryAddress(address);
                    auctionResult.setContactInfo(recipientName + " / " + phone);
                    auctionResult.setDeliveryNote(deliveryNote);
                    break;
                case "PICKUP":
                case "DIRECT_TRADE":
                    auctionResult.setContactInfo(contactName + " / " + contactPhone);
                    auctionResult.setDeliveryNote(tradeNote);
                    break;
            }

            auctionResult.setDeliveryStatus("INFO_SUBMITTED");
            auctionResultService.save(auctionResult);

            // 판매자에게 알림 발송
            notificationService.sendTransactionInfoNotification(auctionResult);

            redirectAttributes.addFlashAttribute("message", "거래 정보가 성공적으로 전달되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/auction/transaction-status/" + auctionResultId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "처리 중 오류가 발생했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/auction/completed/" + auctionResultId;
        }
    }

    // 거래 진행 상황 페이지
    @GetMapping("/auction/transaction-status/{auctionResultId}")
    public String showTransactionStatus(@PathVariable Long auctionResultId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        AuctionResult auctionResult = auctionResultService.findById(auctionResultId);
        if (auctionResult == null) {
            return "redirect:/";
        }

        // 로그인 사용자 정보
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        // 접근 권한 확인 (낙찰자 또는 판매자만)
        boolean isWinner = auctionResult.getWinningBid().getMember().getId().equals(member.getId());
        boolean isSeller = auctionResult.getProduct().getMember().getId().equals(member.getId());

        if (!isWinner && !isSeller) {
            return "redirect:/";
        }

        model.addAttribute("auctionResult", auctionResult);
        model.addAttribute("isWinner", isWinner);
        model.addAttribute("isSeller", isSeller);
        model.addAttribute("username", member.getUsername());
        model.addAttribute("isLogin", true);

        return "auction/transaction-status";
    }

    // 배송 상태 업데이트 (판매자용)
    @PostMapping("/auction/update-delivery-status")
    @ResponseBody
    public ResponseEntity<?> updateDeliveryStatus(
            @RequestParam Long auctionResultId,
            @RequestParam String status,
            @RequestParam(required = false) String trackingNumber,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }

        try {
            AuctionResult auctionResult = auctionResultService.findById(auctionResultId);
            if (auctionResult == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "경매 결과를 찾을 수 없습니다."));
            }

            // 로그인 사용자 확인 (판매자만)
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            if (!auctionResult.getProduct().getMember().getId().equals(member.getId())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "권한이 없습니다."));
            }

            // 상태 업데이트
            auctionResult.setDeliveryStatus(status);
            if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
                auctionResult.setTrackingNumber(trackingNumber);
            }
            auctionResultService.save(auctionResult);

            // 낙찰자에게 알림 발송
            notificationService.sendDeliveryStatusNotification(auctionResult, status);

            return ResponseEntity.ok(Map.of("status", "success", "message", "배송 상태가 업데이트되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 거래 완료 확인 (낙찰자용)
    @PostMapping("/auction/confirm-transaction")
    @ResponseBody
    public ResponseEntity<?> confirmTransaction(
            @RequestParam Long auctionResultId,
            @RequestParam(required = false) String review,
            @RequestParam(required = false) Integer rating,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }

        try {
            AuctionResult auctionResult = auctionResultService.findById(auctionResultId);
            if (auctionResult == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "경매 결과를 찾을 수 없습니다."));
            }

            // 로그인 사용자 확인 (낙찰자만)
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);

            if (!auctionResult.getWinningBid().getMember().getId().equals(member.getId())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "권한이 없습니다."));
            }

            // 거래 완료 처리
            auctionResult.setDeliveryStatus("COMPLETED");
            auctionResult.setCompletedAt(LocalDateTime.now());

            if (review != null && !review.trim().isEmpty()) {
                auctionResult.setReview(review);
            }
            if (rating != null) {
                auctionResult.setRating(rating);
            }

            auctionResultService.save(auctionResult);

            // 판매자에게 알림 발송
            notificationService.sendTransactionCompletedNotification(auctionResult);

            return ResponseEntity.ok(Map.of("status", "success", "message", "거래가 완료되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 낙찰 결과 생성 헬퍼 메소드
    private AuctionResult createAuctionResult(Bid winningBid, Product product) {
        AuctionResult auctionResult = new AuctionResult();
        auctionResult.setWinningBid(winningBid);
        auctionResult.setProduct(product);
        auctionResult.setFinalPrice(winningBid.getBidAmount());
        auctionResult.setAuctionEndTime(LocalDateTime.now());
        auctionResult.setDeliveryStatus("PENDING"); // 거래 방법 선택 대기
        return auctionResult;
    }
}