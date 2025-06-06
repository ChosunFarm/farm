package farm.farmshop.entity.product;

import farm.farmshop.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Product {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // 무게 정보 필드 추가
    @Column(name = "gram")
    private int gram;

    // 상품 설명 필드 추가
    @Column(name = "description", length = 2000)
    private String description;

    // 상품 상태 필드 추가
    // null: 기본 상태(검수 신청 전)
    // "pending": 검수 신청됨/검수 대기
    // "approved": 검수 승인됨
    // "rejected": 검수 거부됨
    @Column(name = "status")
    private String status;

    // 경매 관련 필드 추가
    @Column(name = "current_bid_price")
    private Integer currentBidPrice;

    @Column(name = "bid_count")
    private Integer bidCount = 0;

    // Product.java 파일 - Member와의 ManyToOne 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "dtype", insertable = false, updatable = false)
    private String dtype;

    @Column(name = "image_url") // 데이터베이스 컬럼 이름을 지정
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // src/main/java/farm/farmshop/entity/product/Product.java에 추가
    @Column(name = "auction_date")
    private LocalDateTime auctionDate;

    public LocalDateTime getAuctionDate() {
        return auctionDate;
    }

    public void setAuctionDate(LocalDateTime auctionDate) {
        this.auctionDate = auctionDate;
    }

}