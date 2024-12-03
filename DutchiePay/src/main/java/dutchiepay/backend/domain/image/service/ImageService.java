package dutchiepay.backend.domain.image.service;

import org.springframework.stereotype.Service;

@Service
public class ImageService {
    public String processImages(String[] reviewImages) {
        if (reviewImages == null || reviewImages.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reviewImages.length; i++) {
            sb.append(reviewImages[i]);
            if (i < reviewImages.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
