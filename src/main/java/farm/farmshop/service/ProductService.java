package farm.farmshop.service;

import farm.farmshop.entity.product.Product;
import farm.farmshop.repository.ProductRepository;
import farm.farmshop.service.AuctionAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AuctionAlertService auctionAlertService;

    // 상품 저장
    @Transactional
    public Long saveProduct(Product product) {
        productRepository.save(product);
        return product.getId();
    }

    // ID로 상품 조회
    public Product findOne(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.orElse(null);
    }

    // 검수 신청
    @Transactional
    public void requestInspection(Long productId) {
        Product product = findOne(productId);
        if (product != null) {
            product.setStatus("pending");
            productRepository.save(product);
        }
    }

    // 검수 승인
    @Transactional
    public void approveProduct(Long productId) {
        Product product = findOne(productId);
        if (product != null) {
            product.setStatus("approved");
            productRepository.save(product);

            // 실시간 알림: 검수 완료되어 경매 시작
            Long sellerId = product.getMember().getId();
            Long id = product.getId();
            String name = product.getName();
            auctionAlertService.notifyInspectionComplete(sellerId, id, name);
        }
    }

    // 검수 거부
    @Transactional
    public void rejectProduct(Long productId) {
        Product product = findOne(productId);
        if (product != null) {
            product.setStatus("rejected");
            productRepository.save(product);
        }
    }

    // 모든 상품 조회
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findApprovedProducts() {
        return findByStatus("approved");
    }
    public List<Product> findCompletedProducts() {
        return findByStatus("completed");
    }

    // 특정 상태의 상품 조회
    public List<Product> findByStatus(String status) {
        return productRepository.findByStatus(status);
    }

    // 검수 신청 전 상품 조회
    public List<Product> findByStatusNull() {
        return productRepository.findByStatusNull();
    }

    // 회원이 등록한 상품 조회
    public List<Product> findByMemberId(Long memberId) {
        return productRepository.findByMemberId(memberId);
    }

    // 카테고리별 상품 조회
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // 카테고리 및 상태별 상품 조회
    public List<Product> findByCategoryAndStatus(String category, String status) {
        return productRepository.findByCategoryAndStatus(category, status);
    }
}