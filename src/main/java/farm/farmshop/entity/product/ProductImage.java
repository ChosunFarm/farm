package farm.farmshop.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PRODUCT_IMAGES")
@Getter @Setter
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "IMAGE_URL")
    private String imageUrl;
}