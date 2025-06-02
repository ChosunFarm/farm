// MyProductDTO.java
package farm.farmshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyProductDTO {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private String productType; // "과일", "채소", "곡물" 등
    private String weightInfo; // "1kg" 형태로 미리 가공
    private String status; // "판매 완료", "경매 중" 등
    private String statusClass; // CSS 클래스명: "success", "pending" 등
}