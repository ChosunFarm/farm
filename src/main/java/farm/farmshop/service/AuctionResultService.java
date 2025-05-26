// AuctionResultService.java에 추가 메소드
package farm.farmshop.service;

import farm.farmshop.entity.AuctionResult;
import farm.farmshop.repository.AuctionResultRepository;
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

    @Transactional
    public AuctionResult save(AuctionResult auctionResult) {
        return auctionResultRepository.save(auctionResult);
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

    @Transactional
    public void updateDeliveryStatus(Long auctionResultId, String status) {
        AuctionResult auctionResult = findById(auctionResultId);
        if (auctionResult != null) {
            auctionResult.setDeliveryStatus(status);
            save(auctionResult);
        }
    }
}