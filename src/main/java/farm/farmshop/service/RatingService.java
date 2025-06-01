package farm.farmshop.service;

import farm.farmshop.entity.AuctionResult;
import farm.farmshop.repository.AuctionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final AuctionResultRepository auctionResultRepository;

    public double getAvgRating(Long sellerId) {
        List<AuctionResult> ratings = auctionResultRepository.findRatingsBySeller(sellerId);
        System.out.println("▶▶▶ 리포지토리에서 가져온 평점 건수 = " + ratings.size());
        for (AuctionResult ar : ratings) {
            System.out.println("    - rating=" + ar.getRating());
        }
        return ratings.stream()
                .mapToInt(AuctionResult::getRating)
                .average()
                .orElse(0.0);
    }

    public List<String> getStarIcons(double avgRating) {
        List<String> starIcons = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            if (avgRating >= i) {
                starIcons.add("full");
            } else if (avgRating >= i - 0.5) {
                starIcons.add("half");
            } else {
                starIcons.add("empty");
            }
        }
        return starIcons;
    }
}
