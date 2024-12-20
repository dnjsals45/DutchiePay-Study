package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.dto.*;
import dutchiepay.backend.domain.community.exception.CommunityErrorCode;
import dutchiepay.backend.domain.community.exception.CommunityException;
import dutchiepay.backend.domain.community.repository.PurchaseRepository;
import dutchiepay.backend.domain.community.repository.QPurchaseRepositoryImpl;
import dutchiepay.backend.entity.Purchase;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final QPurchaseRepositoryImpl qPurchaseRepositoryImpl;
    private final PurchaseRepository purchaseRepository;
    private final PostHitService postHitService;

    /**
     * 나눔/거래 리스트 조회
     * @param category trade | share | null
     * @param word  검색 시 키워드
     * @param limit    한 번에 조회하는 페이지 양
     * @param cursor   다음부터 조회 할 purchaseId
     * @return 게시글 리스트가 담긴 dto
     */
    public PurchaseListResponseDto getPurchaseList(User user, String category, String word, int limit, Long cursor) {

        return qPurchaseRepositoryImpl.getPurchaseList(user,category, word, limit, cursor);
    }

    /**
     * 게시글 단건 조회
     * @param purchaseId 조회 할 게시글 Id
     * @return 게시글 내용이 담긴 dto
     */
    public PurchaseResponseDto getPurchase(User user, Long purchaseId) {
        postHitService.increaseHitCount(user, "purchase", purchaseId);
        return qPurchaseRepositoryImpl.getPurchase(purchaseId);
    }

    /**
     * 게시글 작성
     * @param user 작성자
     * @param requestDto 게시글 내용이 담긴 dto
     * @return 생성된 purchase Id
     */
    @Transactional
    public Map<String, Long> createPurchase(User user, CreatePurchaseRequestDto requestDto) {
        if (requestDto.getTitle().length() > 60) throw new CommunityException(CommunityErrorCode.OVER_TITLE_LENGTH);

        return Map.of("purchaseId",
                purchaseRepository.save(Purchase.builder().user(user)
                        .title(requestDto.getTitle())
                        .contents(requestDto.getContent())
                        .price(requestDto.getPrice())
                        .meetingPlace(requestDto.getMeetingPlace())
                        .latitude(requestDto.getLatitude())
                        .longitude(requestDto.getLongitude())
                        .goods(requestDto.getGoods())
                        .thumbnail(requestDto.getThumbnail())
                        .images(String.join(",", requestDto.getImages()))
                        .category(requestDto.getCategory())
                        .location(user.getLocation())
                        .state("모집중").build()).getPurchaseId());
    }

    /**
     * 게시글 수정용 단건 조회
     * @param purchaseId 조회할 게시글 Id
     * @return 수정용 데이터가 담긴 dto
     */
    public PurchaseForUpdateDto getPurchaseForUpdate(User user,Long purchaseId) {

        findPurchaseAndValidateWriter(user, purchaseId);
        return qPurchaseRepositoryImpl.getPurchaseForUpdate(purchaseId);
    }

    /**
     * 게시글 수정
     * @param user 작성자
     * @param updateDto 게시글 내용이 담긴 dto
     */
    @Transactional
    public void updatePurchase(User user, UpdatePurchaseRequestDto updateDto) {
        findPurchaseAndValidateWriter(user, updateDto.getPurchaseId()).updatePurchase(updateDto);
    }

    /**
     * 게시글 삭제
     * @param user 작성자
     * @param purchaseId 삭제할 게시글 Id
     */
    @Transactional
    public void deletePurchase(User user, Long purchaseId) {
        purchaseRepository.delete(findPurchaseAndValidateWriter(user, purchaseId));
    }

    /**
     * 게시글을 찾고 요청한 user와 작성자를 검증하는 메서드
     * 게시글이 없거나 작성자가 일치하지 않으면 Exception 발생
     * @param user 요청한 user
     * @param purchaseId 찾을 게시글 Id
     * @return 찾은 게시글
     */
    private Purchase findPurchaseAndValidateWriter(User user, Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.CANNOT_FOUND_POST));
        if (!user.getUserId().equals(purchase.getUser().getUserId()))
            throw new CommunityException(CommunityErrorCode.UNMATCHED_WRITER);
        return purchase;
    }
}
