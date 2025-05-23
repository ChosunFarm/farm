package farm.farmshop.service;

import farm.farmshop.entity.AuctionResult;
import farm.farmshop.entity.Bid;
import farm.farmshop.entity.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    // 낙찰 알림 발송
    public void sendWinningNotification(Bid winningBid, Product product) {
        try {
            // 낙찰자에게 알림
            String winnerMessage = String.format(
                    "축하합니다! '%s' 상품에 낙찰되었습니다. 낙찰가: %,d원",
                    product.getName(),
                    winningBid.getBidAmount()
            );
            log.info("낙찰자 알림 발송: {} -> {}", winningBid.getMember().getEmail(), winnerMessage);

            // 판매자에게 알림
            String sellerMessage = String.format(
                    "'%s' 상품이 낙찰되었습니다. 낙찰자: %s, 낙찰가: %,d원",
                    product.getName(),
                    winningBid.getMember().getUsername(),
                    winningBid.getBidAmount()
            );
            log.info("판매자 알림 발송: {} -> {}", product.getMember().getEmail(), sellerMessage);

            // 실제 구현시에는 이메일, SMS, 푸시 알림 등을 발송

        } catch (Exception e) {
            log.error("낙찰 알림 발송 중 오류 발생", e);
        }
    }

    // 거래 정보 제출 알림
    public void sendTransactionInfoNotification(AuctionResult auctionResult) {
        try {
            String message = String.format(
                    "'%s' 상품의 거래 정보가 제출되었습니다. 거래 방법: %s",
                    auctionResult.getProduct().getName(),
                    getTransactionMethodName(auctionResult.getTransactionMethod())
            );
            log.info("거래 정보 알림 발송: {} -> {}",
                    auctionResult.getProduct().getMember().getEmail(), message);

        } catch (Exception e) {
            log.error("거래 정보 알림 발송 중 오류 발생", e);
        }
    }

    // 배송 상태 변경 알림
    public void sendDeliveryStatusNotification(AuctionResult auctionResult, String status) {
        try {
            String message = String.format(
                    "'%s' 상품의 배송 상태가 '%s'(으)로 변경되었습니다.",
                    auctionResult.getProduct().getName(),
                    getDeliveryStatusName(status)
            );
            log.info("배송 상태 알림 발송: {} -> {}",
                    auctionResult.getWinningBid().getMember().getEmail(), message);

        } catch (Exception e) {
            log.error("배송 상태 알림 발송 중 오류 발생", e);
        }
    }

    // 거래 완료 알림
    public void sendTransactionCompletedNotification(AuctionResult auctionResult) {
        try {
            String message = String.format(
                    "'%s' 상품의 거래가 완료되었습니다. 이용해 주셔서 감사합니다.",
                    auctionResult.getProduct().getName()
            );
            log.info("거래 완료 알림 발송: {} -> {}",
                    auctionResult.getProduct().getMember().getEmail(), message);

        } catch (Exception e) {
            log.error("거래 완료 알림 발송 중 오류 발생", e);
        }
    }

    // 거래 방법명 변환
    private String getTransactionMethodName(String method) {
        switch (method) {
            case "DELIVERY": return "택배 배송";
            case "PICKUP": return "직접 수령";
            case "DIRECT_TRADE": return "직거래";
            default: return "알 수 없음";
        }
    }

    // 배송 상태명 변환
    private String getDeliveryStatusName(String status) {
        switch (status) {
            case "PENDING": return "대기중";
            case "INFO_SUBMITTED": return "정보 제출됨";
            case "CONFIRMED": return "확인됨";
            case "IN_TRANSIT": return "배송중";
            case "DELIVERED": return "배송완료";
            case "COMPLETED": return "거래완료";
            default: return "알 수 없음";
        }
    }
}
