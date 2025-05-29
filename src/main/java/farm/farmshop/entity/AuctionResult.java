// AuctionResult.java 업데이트 - 진행률 필드 추가
package farm.farmshop.entity;

import farm.farmshop.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction_results")
@Getter @Setter
public class AuctionResult {

    @Id @GeneratedValue
    @Column(name = "auction_result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id")
    private Bid winningBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "final_price")
    private Integer finalPrice;

    @Column(name = "auction_end_time")
    private LocalDateTime auctionEndTime;

    @Column(name = "delivery_status")
    private String deliveryStatus; // PENDING, INFO_SUBMITTED, CONFIRMED, IN_TRANSIT, DELIVERED, COMPLETED

    @Column(name = "transaction_method")
    private String transactionMethod; // DELIVERY, PICKUP, DIRECT_TRADE

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(name = "contact_info", length = 200)
    private String contactInfo;

    @Column(name = "delivery_note", length = 1000)
    private String deliveryNote;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "review", length = 1000)
    private String review;

    @Column(name = "rating")
    private Integer rating; // 1-5점

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 진행률 계산용 transient 필드 (DB에 저장되지 않음)
    @Transient
    private Integer progressPercentage;

    // 진행률 자동 계산 메소드
    public Integer getProgressPercentage() {
        if (progressPercentage != null) {
            return progressPercentage;
        }

        if (deliveryStatus == null) {
            return 10;
        }

        switch (deliveryStatus) {
            case "PENDING":
                return 20;
            case "INFO_SUBMITTED":
                return 40;
            case "CONFIRMED":
                return 60;
            case "IN_TRANSIT":
                return 80;
            case "DELIVERED":
                return 90;
            case "COMPLETED":
                return 100;
            default:
                return 10;
        }
    }

    // 거래 상태 한글명 반환
    @Transient
    public String getDeliveryStatusName() {
        if (deliveryStatus == null) {
            return "대기중";
        }

        switch (deliveryStatus) {
            case "PENDING":
                return "대기중";
            case "INFO_SUBMITTED":
                return "정보 제출됨";
            case "CONFIRMED":
                return "확인됨";
            case "IN_TRANSIT":
                return "배송중";
            case "DELIVERED":
                return "배송완료";
            case "COMPLETED":
                return "거래완료";
            default:
                return "알 수 없음";
        }
    }

    // 거래 방법 한글명 반환
    @Transient
    public String getTransactionMethodName() {
        if (transactionMethod == null) {
            return "미선택";
        }

        switch (transactionMethod) {
            case "DELIVERY":
                return "택배 배송";
            case "PICKUP":
                return "직접 수령";
            case "DIRECT_TRADE":
                return "직거래";
            default:
                return "알 수 없음";
        }
    }

    // 내가 구매자인지 판매자인지 확인하는 메소드
    @Transient
    public boolean isWinner(Long memberId) {
        return winningBid != null &&
                winningBid.getMember() != null &&
                winningBid.getMember().getId().equals(memberId);
    }

    @Transient
    public boolean isSeller(Long memberId) {
        return product != null &&
                product.getMember() != null &&
                product.getMember().getId().equals(memberId);
    }
}