package farm.farmshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BidUpdateDTO {
    private Long productId;
    private String bidderName;
    private Integer bidAmount;
    private LocalDateTime bidTime;
    private Integer bidCount;
    private String status;
    private String message;

    // 생성자
    public BidUpdateDTO() {}

    public BidUpdateDTO(Long productId, String bidderName, Integer bidAmount,
                        LocalDateTime bidTime, Integer bidCount, String status, String message) {
        this.productId = productId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.bidCount = bidCount;
        this.status = status;
        this.message = message;
    }
}