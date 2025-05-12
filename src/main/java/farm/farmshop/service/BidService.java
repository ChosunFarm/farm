package farm.farmshop.service;

import farm.farmshop.entity.Bid;
import farm.farmshop.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;

    // 입찰 정보 저장
    @Transactional
    public Long saveBid(Bid bid) {
        bidRepository.save(bid);
        return bid.getId();
    }

    // ID로 입찰 정보 조회
    public Bid findById(Long bidId) {
        Optional<Bid> bidOpt = bidRepository.findById(bidId);
        return bidOpt.orElse(null);
    }

    // 상품별 입찰 내역 조회
    public List<Bid> findByProductId(Long productId) {
        return bidRepository.findByProductId(productId);
    }

    // 회원별 입찰 내역 조회
    public List<Bid> findByMemberId(Long memberId) {
        return bidRepository.findByMemberId(memberId);
    }

    // 입찰 수락 처리
    @Transactional
    public void acceptBid(Bid bid) {
        bid.setStatus("accepted");
        bidRepository.save(bid);

        // 같은 상품의 다른 입찰들은 거부 처리
        List<Bid> otherBids = bidRepository.findByProductId(bid.getProduct().getId());
        for (Bid otherBid : otherBids) {
            if (!otherBid.getId().equals(bid.getId())) {
                otherBid.setStatus("rejected");
                bidRepository.save(otherBid);
            }
        }
    }

    // 입찰 거부 처리
    @Transactional
    public void rejectBid(Bid bid) {
        bid.setStatus("rejected");
        bidRepository.save(bid);
    }

    // 판매자의 상품에 대한 입찰 내역 조회
    public List<Bid> findByProductSellerId(Long sellerId) {
        return bidRepository.findByProductSellerId(sellerId);
    }
}