package farm.farmshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class MyBidDTO {
    private Long id;
    private Integer bidAmount;
    private LocalDateTime bidTime;
    private String status;
    private String statusClass;
    private String statusText;

    // 상품 정보
    private Long productId;
    private String productName;
    private String imageUrl;
    private String weightInfo;

    // 입찰자 정보 (판매자용)
    private String bidderName;
}