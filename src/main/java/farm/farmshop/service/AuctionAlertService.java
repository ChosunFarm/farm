package farm.farmshop.service;

import farm.farmshop.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionAlertService {

    private final AlertService alertService;

    /**
     * 구매자 입장: 본인이 구매 완료되었을 때
     */
    public void notifyPurchaseCompleted(Long buyerId, Long productId, String productName) {
        String msg = productName + " 상품이 구매되었습니다!";
        alertService.sendAlert(buyerId, productId, "PURCHASE", msg);
    }

    /**
     * 구매자 입장: 입찰가가 갱신되었을 때
     */
    public void notifyBidUpdatedForBuyer(Long bidderId, Long productId, String productName, int newBid) {
        String msg = productName + " 새로운 가격 " + newBid + "원에 입찰되었습니다!";
        alertService.sendAlert(bidderId, productId, "BID_UPDATE", msg);
    }

    /**
     * 구매자 입장: 거래 완료(수령 확인) 시
     */
    public void notifyTransactionCompletedForBuyer(Long buyerId, Long productId, String productName) {
        String msg = productName + " 거래가 완료되었습니다!";
        alertService.sendAlert(buyerId, productId, "COMPLETE_BUYER", msg);
    }

    /**
     * 판매자 입장: 검수 완료되어 경매 시작 시
     */
    public void notifyInspectionComplete(Long sellerId, Long productId, String productName) {
        String msg = productName + " 상품이 검수가 완료되어 경매가 시작되었습니다!";
        alertService.sendAlert(sellerId, productId, "INSPECTION_COMPLETE", msg);
    }

    /**
     * 판매자 입장: 자신의 상품에 새 입찰이 들어왔을 때
     */
    public void notifyBidUpdatedForSeller(Long sellerId, Long productId, String productName, int newBid) {
        String msg = productName + " 가격이 " + newBid + "원에 갱신되었습니다!";
        alertService.sendAlert(sellerId, productId, "BID_UPDATE_SELLER", msg);
    }

    /**
     * 판매자 입장: 본인이 판매 완료 시
     */
    public void notifySaleCompletedForSeller(Long sellerId, Long productId, String productName) {
        String msg = productName + " 상품이 낙찰되었습니다!";
        alertService.sendAlert(sellerId, productId, "COMPLETE_SELLER", msg);
    }

    public void notifyOutbidForBuyer(Long previousBidderId,
        Long productId,
        String productName,
        int newHighestBid) {
        String msg = productName + " 경매에서 더 높은 가격 " + newHighestBid + "원에 입찰되었습니다!";
        alertService.sendAlert(previousBidderId, productId, "OUTBID", msg);
    }

    /**
    * 타 사용자가 입찰했을 때 기존 참여자에게 알림
    */
    public void notifyBidByOther(Long recipientId,
        Long productId,
        String productName,
        int newBid,
        String bidderName) {
        String msg = bidderName + "님이 " + productName + " 상품에 새로운 가격 " + newBid + "원으로 입찰하였습니다!";
        alertService.sendAlert(recipientId, productId, "BID_BY_OTHER", msg);
    }

    public void notifyBidFailedForBuyer(Long bidderId,
        Long productId,
        String productName) {
    String msg = productName + " 상품의 낙찰에 실패하셨습니다!";
    alertService.sendAlert(bidderId, productId, "BID_FAILED", msg);
    }
}
