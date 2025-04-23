package farm.farmshop.dto;

import farm.farmshop.entity.product.Product;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductForm {
    private String name;
    private int price;
    private int stockQuantity;
    private String category; // 카테고리 구분(Fruit, Vegetable, Grain)

    // 추가적으로 필요한 필드들 (예: 이미지 파일 등)
    private String imageUrl;
    private String description;
}