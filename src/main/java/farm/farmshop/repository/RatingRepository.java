package farm.farmshop.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import farm.farmshop.entity.AuctionResult;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RatingRepository extends JpaRepository<AuctionResult, Long> {

    @Query("SELECT AVG(ar.rating) FROM AuctionResult ar " +
           "WHERE ar.rating IS NOT NULL AND ar.product.member.id = :sellerId")
    Double getAvgRatingBySellerId(@Param("sellerId") Long sellerId);
}
