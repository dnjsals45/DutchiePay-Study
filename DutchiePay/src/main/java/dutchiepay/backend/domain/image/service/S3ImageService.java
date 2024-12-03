package dutchiepay.backend.domain.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import dutchiepay.backend.domain.image.dto.GetPreSignedUrlResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /**
     * presigned url 발급
     * @author     dnjsals45
     * @version    1.0.0
     * @since      1.0.0
     * @param      fileName 파일명
     * @return     GetPreSignedUrlResponseDto(presigned url)
     */
    public GetPreSignedUrlResponseDto getPreSignedUrl(String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return GetPreSignedUrlResponseDto.builder()
                .uploadUrl(url.toString())
                .build();
    }

    /**
     * 파일 업로드용(POST) presigned url 발급
     * @author     dnjsals45
     * @version    1.0.0
     * @since      1.0.0
     * @param      bucket 버킷명
     * @param      fileName 파일명
     * @return     GeneratePresignedUrlRequest
     */
    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
       GeneratePresignedUrlRequest generatePresignedUrlRequest =
               new GeneratePresignedUrlRequest(bucket, fileName)
                       .withMethod(HttpMethod.PUT)
                       .withExpiration(getPresignedUrlExpiration());
       generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
       return generatePresignedUrlRequest;
    }

    /**
     * presigned url 만료시간 설정(15분)
     * @author     dnjsals45
     * @version    1.0.0
     * @since      1.0.0
     * @return     presigned url 만료시간
     */
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 15;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
