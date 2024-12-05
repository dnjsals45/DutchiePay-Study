package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.dto.GetMartListResponseDto;
import dutchiepay.backend.domain.community.dto.GetMartResponseDto;
import dutchiepay.backend.domain.community.repository.ShareRepository;
import dutchiepay.backend.domain.community.dto.CreateMartRequestDto;
import dutchiepay.backend.domain.community.dto.UpdateMartRequestDto;
import dutchiepay.backend.entity.Share;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MartService {

    private final ShareRepository shareRepository;

    @Transactional
    public void createMart(CreateMartRequestDto req) {
        validateTitle(req.getTitle());
        createMartEntity(req);
    }

    @Transactional
    public void updateMart(UpdateMartRequestDto req) {
        validateTitle(req.getTitle());
        updateMartEntity(req);
    }

    @Transactional
    public void deleteMart(Long shareId) {
        shareRepository.softDelete(shareId);
    }

    public GetMartResponseDto getMartByShareId(Long shareId) {
        validateShare(shareId);
        return shareRepository.getMartByShareId(shareId);
    }

    public GetMartListResponseDto getMartList(User user, String category, Long cursor, Integer limit) {
        validateCategory(category);
        return shareRepository.getMartList(user, category, cursor, limit);
    }

    private void updateMartEntity(UpdateMartRequestDto req) {
        Share share = shareRepository.findById(req.getShareId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        share.update(req);
    }

    private void validateTitle(String title) {
        if (title.length() > 60) {
            throw new IllegalArgumentException("제목은 60자 이하로 입력해주세요.");
        }
    }

    private void createMartEntity(CreateMartRequestDto req) {
        Share newShare = Share.builder()
                .title(req.getTitle())
                .date(req.getDate())
                .maximum(req.getMaximum())
                .meetingPlace(req.getMeetingPlace())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .contents(req.getContent())
                .thumbnail(req.getThumbnail())
                .category(req.getCategory())
                .build();

        shareRepository.save(newShare);
    }

    private void validateCategory(String category) {
        if (!category.equals("mart") && !category.equals("delivery")) {
            throw new IllegalArgumentException("카테고리는 마트 또는 배달로 입력해주세요.");
        }
    }

    private void validateShare(Long shareId) {
        if (!shareRepository.existsById(shareId)) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }
    }
}
