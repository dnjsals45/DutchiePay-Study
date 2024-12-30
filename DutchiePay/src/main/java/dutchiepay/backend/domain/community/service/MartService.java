package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.dto.*;
import dutchiepay.backend.domain.community.exception.CommunityErrorCode;
import dutchiepay.backend.domain.community.exception.CommunityException;
import dutchiepay.backend.domain.community.repository.ShareRepository;
import dutchiepay.backend.entity.Share;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MartService {
    private final CommunityUtilService communityUtilService;
    private final PostHitService postHitService;
    private final ShareRepository shareRepository;

    @Transactional
    public CreateShareResponseDto createMart(User user, CreateMartRequestDto req) {
        validateTitle(req.getTitle());
        validateCategory(req.getCategory());
        String description = communityUtilService.validatePostLength(req.getContent());
        return CreateShareResponseDto.from(createShareEntity(req, user, description));
    }

    @Transactional
    public void updateMart(UpdateMartRequestDto req) {
        validateTitle(req.getTitle());
        updateMartEntity(req);
    }

    @Transactional
    public void deleteMart(Long shareId) {
        validateShare(shareId);
        shareRepository.softDelete(shareId);
    }

    public GetMartResponseDto getMartByShareId(User user, Long shareId) {
        postHitService.increaseHitCount(user, "share", shareId);
        validateShare(shareId);
        return shareRepository.getMartByShareId(shareId);
    }

    public GetMartUpdateResponseDto getMartByShareIdForUpdate(Long shareId) {
        validateShare(shareId);
        return shareRepository.getMartByShareIdForUpdate(shareId);
    }

    public GetMartListResponseDto getMartList(User user, String category, String word, Long cursor, Integer limit) {
        validateCategory(category);
        return shareRepository.getMartList(user, category, word, cursor, limit);
    }

    private void updateMartEntity(UpdateMartRequestDto req) {
        Share share = shareRepository.findById(req.getShareId())
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_POST));

        share.update(req);
    }

    private void validateTitle(String title) {
        if (title.length() > 60) {
            throw new CommunityException(CommunityErrorCode.OVER_TITLE_LENGTH);
        }
    }

    private Share createShareEntity(CreateMartRequestDto req, User user, String description) {
        Share newShare = Share.builder()
                .user(user)
                .title(req.getTitle())
                .date(req.getDate())
                .maximum(req.getMaximum())
                .meetingPlace(req.getMeetingPlace())
                .location(user.getLocation())
                .state("모집중")
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .contents(req.getContent())
                .description(description)
                .thumbnail(req.getThumbnail())
                .category(req.getCategory())
                .images(String.join(",", req.getImages()))
                .now(1)
                .hits(0)
                .build();

        shareRepository.save(newShare);

        return newShare;
    }

    private void validateCategory(String category) {
        if (category != null && !category.equals("mart") && !category.equals("delivery")) {
            throw new CommunityException(CommunityErrorCode.INVALID_CATEGORY);
        }
    }

    private void validateShare(Long shareId) {
        if (!shareRepository.existsById(shareId)) {
            throw new CommunityException(CommunityErrorCode.INVALID_POST);
        }
    }

    @Transactional
    public void changeStatus(ChangeStatusRequestDto req) {
        Share share = shareRepository.findById(req.getPostId())
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_POST));
        if (share.getState().equals("모집완료")) throw new CommunityException(CommunityErrorCode.ALREADY_DONE);
        share.changeStatus(req.getStatus());
    }
}
