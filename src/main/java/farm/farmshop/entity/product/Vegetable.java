package farm.farmshop.entity.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("V")
@Getter @Setter
public class Vegetable extends Product {
    private String vegetableName;
    // gram 필드는 상위 클래스(Product)로 이동
}