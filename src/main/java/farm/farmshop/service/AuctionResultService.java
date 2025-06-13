package farm.farmshop.service;

import farm.farmshop.entity.AuctionResult;
import farm.farmshop.entity.Bid;
import farm.farmshop.repository.AuctionResultRepository;
import farm.farmshop.repository.BidRepository;
import farm.farmshop.service.AuctionAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionResultService {

    private final AuctionResultRepository auctionResultRepository;
    private final BidRepository bidRepository;
    private final AuctionAlertService auctionAlertService;

    @Transactional
    public AuctionResult save(AuctionResult auctionResult) {
        // 먼저 저장
        AuctionResult saved = auctionResultRepository.save(auctionResult);

        // 구매자 거래 완료 알림
        Long buyerId   = saved.getWinningBid().getMember().getId();
        // 판매자 판매 완료 알림
        Long sellerId  = saved.getProduct().getMember().getId();
        Long productId = saved.getProduct().getId();
        String name    = saved.getProduct().getName();

        auctionAlertService.notifyTransactionCompletedForBuyer(
            buyerId, productId, name
        );
        auctionAlertService.notifySaleCompletedForSeller(
            sellerId, productId, name
        );

        List<Bid> allBids = bidRepository.findByProductId(productId);
        for (Bid bid : allBids) {
            Long bidderId = bid.getMember().getId();
            if (!bidderId.equals(buyerId)) {
                auctionAlertService.notifyBidFailedForBuyer(
                    bidderId, productId, name
                );
            }
        }

        return saved;
    }

    public AuctionResult findById(Long id) {
        return auctionResultRepository.findById(id).orElse(null);
    }

    public List<AuctionResult> findByWinnerId(Long memberId) {
        return auctionResultRepository.findByWinnerId(memberId);
    }

    public List<AuctionResult> findBySellerId(Long sellerId) {
        return auctionResultRepository.findBySellerId(sellerId);
    }

    public AuctionResult findByProductId(Long productId) {
        return auctionResultRepository.findByProductId(productId);
    }

    public List<AuctionResult> findByDeliveryStatus(String status) {
        return auctionResultRepository.findByDeliveryStatus(status);
    }

    // 30일 전에 완료된 거래 조회 (개인정보 삭제용)
    public List<AuctionResult> findCompletedBefore(LocalDateTime date) {
        return auctionResultRepository.findCompletedBefore(date);
    }

    // 진행 중인 거래 조회 (완료되지 않은 것들)
    public List<AuctionResult> findOngoingByMemberId(Long memberId) {
        return auctionResultRepository.findOngoingByMemberId(memberId);
    }
 
    // 완료된 거래 조회
    public List<AuctionResult> findCompletedByMemberId(Long memberId) {
        return auctionResultRepository.findCompletedByMemberId(memberId);
    }
    public List<AuctionResult> findRatingsBySeller(Long sellerId) {
        return auctionResultRepository.findByProduct_Member_IdAndRatingIsNotNull(sellerId);
    }

    @Transactional
    public void updateDeliveryStatus(Long auctionResultId, String status) {
        AuctionResult auctionResult = findById(auctionResultId);
        if (auctionResult != null) {
            auctionResult.setDeliveryStatus(status);
            // save 메서드를 통해 알림 로직 포함
            save(auctionResult);

            if ("COMPLETED".equals(status)) {
                Long buyerId   = auctionResult.getWinningBid().getMember().getId();
                Long sellerId  = auctionResult.getProduct().getMember().getId();
                Long productId = auctionResult.getProduct().getId();
                String name    = auctionResult.getProduct().getName();

                auctionAlertService.notifyTransactionCompletedForBuyer(
                    buyerId, productId, name
                );
                auctionAlertService.notifySaleCompletedForSeller(
                    sellerId, productId, name
                );
            }
        }
    }
}