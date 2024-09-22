package dutchiepay.backend.domain.image.controller;

import dutchiepay.backend.domain.image.dto.GetPreSignedUrlRequestDto;
import dutchiepay.backend.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이미지 API", description = "AWS S3 관련 API")
@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "이미지 업로드(구현 완료)", description = "파일 명을 바탕으로 한 presigned url을 반환.(유효기간 15분)")
    @PostMapping
    public ResponseEntity<?> getPreSignedUrl(@RequestBody GetPreSignedUrlRequestDto request) {
        return ResponseEntity.ok().body(imageService.getPreSignedUrl(request.getFileName()));
    }
}
