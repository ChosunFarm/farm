package farm.farmshop.controller;

import farm.farmshop.dto.AlertDto;
import farm.farmshop.entity.AuctionResult;
import farm.farmshop.entity.Member;
import farm.farmshop.repository.MemberRepository;
import farm.farmshop.service.AuctionResultService;
import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class TransactionController {

    private final MemberRepository memberRepository;
    private final AuctionResultService auctionResultService;
    private final AlertService alertService;

    // 거래 현황 통합 페이지
    @GetMapping("/transactions")
    public String showTransactions(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", member.getUsername());
        model.addAttribute("isLogin", true);
        model.addAttribute("currentMemberId", member.getId());

                Long memberId = member.getId();
        model.addAttribute("memberId", memberId);          

        long unreadCnt = alertService.countUnread(memberId);
        model.addAttribute("alertCount", unreadCnt);

        List<AlertDto> unreadList = alertService.getUnreadAlerts(memberId)
                                               .stream()
                                               .map(AlertDto::fromEntity)
                                               .toList();
        model.addAttribute("alertList", unreadList);

        // 나의 낙찰 내역 (구매자 입장)
        List<AuctionResult> myWinning = auctionResultService.findByWinnerId(member.getId());

        // 나의 판매 내역 (판매자 입장)
        List<AuctionResult> mySelling = auctionResultService.findBySellerId(member.getId());

        // 진행 중인 거래 (완료되지 않은 것들)
        List<AuctionResult> ongoingTransactions = myWinning.stream()
                .filter(ar -> !"COMPLETED".equals(ar.getDeliveryStatus()))
                .collect(Collectors.toList());

        ongoingTransactions.addAll(
                mySelling.stream()
                        .filter(ar -> !"COMPLETED".equals(ar.getDeliveryStatus()))
                        .collect(Collectors.toList())
        );

        // 완료된 거래
        List<AuctionResult> completedTransactions = myWinning.stream()
                .filter(ar -> "COMPLETED".equals(ar.getDeliveryStatus()))
                .collect(Collectors.toList());

        completedTransactions.addAll(
                mySelling.stream()
                        .filter(ar -> "COMPLETED".equals(ar.getDeliveryStatus()))
                        .collect(Collectors.toList())
        );

        // 진행률 계산 추가
        ongoingTransactions.forEach(this::calculateProgress);

        model.addAttribute("ongoingTransactions", ongoingTransactions);
        model.addAttribute("completedTransactions", completedTransactions);
        model.addAttribute("myWinning", myWinning);
        model.addAttribute("mySelling", mySelling);
        model.addAttribute("ongoingCount", ongoingTransactions.size());

        return "mypage/transactions";
    }

    // TransactionController.java에서 진행률 계산 로직 추가
    private void calculateProgress(AuctionResult auctionResult) {
        String status = auctionResult.getDeliveryStatus();
        int progress = switch (status != null ? status : "PENDING") {
            case "PENDING" -> 20;
            case "INFO_SUBMITTED" -> 40;
            case "CONFIRMED" -> 60;
            case "IN_TRANSIT" -> 80;
            case "DELIVERED" -> 90;
            case "COMPLETED" -> 100;
            default -> 10;
        };
        auctionResult.setProgressPercentage(progress);
    }

    // 나의 낙찰 내역만 보는 페이지
    @GetMapping("/my-winning")
    public String myWinningHistory(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            model.addAttribute("username", member.getUsername());
            model.addAttribute("isLogin", true);

            List<AuctionResult> winningResults = auctionResultService.findByWinnerId(member.getId());
            model.addAttribute("winningResults", winningResults);
        }

        return "mypage/my-winning";
    }

    // 나의 판매 내역만 보는 페이지
    @GetMapping("/my-selling")
    public String mySellingHistory(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            model.addAttribute("username", member.getUsername());
            model.addAttribute("isLogin", true);

            List<AuctionResult> sellingResults = auctionResultService.findBySellerId(member.getId());
            model.addAttribute("sellingResults", sellingResults);
        }

        return "mypage/my-selling";
    }
}