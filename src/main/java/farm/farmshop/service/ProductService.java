package farm.farmshop.service;

import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long saveProduct(Product product) {
        productRepository.save(product);
        return product.getId();
    }

    // 여기에 다른 필요한 메서드들을 추가
}