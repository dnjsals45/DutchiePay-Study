package dutchiepay.backend.domain.profile.service;

import dutchiepay.backend.domain.commerce.repository.ScoreRepository;
import dutchiepay.backend.entity.Order;
import dutchiepay.backend.entity.Review;
import dutchiepay.backend.entity.Score;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;

    public void updateScore(Order order, int rating) {
        Score score = scoreRepository.findByBuy(order.getBuy());
        if (score == null) {
            createNewScore(order, rating);
        } else {
            score.addReview(rating);
        }
    }

    public void updateScoreOnUpdate(Review review, int oldRating, int newRating) {
        Score score = scoreRepository.findByBuy(review.getOrder().getBuy());
        if (score != null) {
            score.updateReview(oldRating, newRating);
        }
    }

    public void updateScoreOnDelete(Review review, int deleteRating) {
        Score score = scoreRepository.findByBuy(review.getOrder().getBuy());
        if (score != null) {
            score.removeReview(deleteRating);
        }
    }

    private void createNewScore(Order order, int rating) {
        Score newScore = Score.builder()
                .buy(order.getBuy())
                .one(rating == 1 ? 1 : 0)
                .two(rating == 2 ? 1 : 0)
                .three(rating == 3 ? 1 : 0)
                .four(rating == 4 ? 1 : 0)
                .five(rating == 5 ? 1 : 0)
                .count(1)
                .build();

        scoreRepository.save(newScore);
    }
}
