package farm.farmshop.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

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




//    @ManyToMany(mappedBy = "items")
//    private List<Category> categories = new ArrayList<>();
}
