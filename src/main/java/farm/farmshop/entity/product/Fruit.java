package farm.farmshop.entity.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("F")
@Getter @Setter
public class Fruit extends Product{

    private String FruitName;
    private int gram;
}
