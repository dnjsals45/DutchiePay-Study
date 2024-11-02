package dutchiepay.backend.domain.order.exception;

import lombok.Getter;

@Getter
public class ReviewErrorException extends RuntimeException {
    private final ReviewErrorCode reviewErrorCode;

    public ReviewErrorException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode.getMessage());
        this.reviewErrorCode = reviewErrorCode;
    }

    @Override
    public String toString() {
        return String.format("ReviewErrorException(code=%s, message=%s)",
                reviewErrorCode.name(), reviewErrorCode.getMessage());
    }
}
