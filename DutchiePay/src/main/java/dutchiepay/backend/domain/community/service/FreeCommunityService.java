package dutchiepay.backend.domain.community.service;

import dutchiepay.backend.domain.community.dto.*;
import dutchiepay.backend.domain.community.repository.QFreeRepositoryImpl;
import dutchiepay.backend.domain.notice.service.NoticeService;
import dutchiepay.backend.entity.Comment;
import dutchiepay.backend.entity.Free;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static dutchiepay.backend.domain.community.service.CommunityUtilService.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeCommunityService {

    private final CommunityUtilService communityUtilService;
    private final PostHitService postHitService;
    private final QFreeRepositoryImpl qFreeRepository;
    private final NoticeService noticeService;


    /**
     * 게시글 리스트 조회
     *
     * @return 게시글 리스트 dto
     */
    public FreeListResponseDto getFreeList(String category, String filter, int limit, Long cursor) {

        return qFreeRepository.getFreeLists(category, filter, limit, cursor);
    }

    /**
     * 게시글 단건 조회
     *
     * @param user  조회를 요청한 user
     * @param freeId 조회할 게시글 Id
     * @return 게시글 상세 정보 dto
     */
    public FreePostResponseDto getFreePost(User user, Long freeId) {

        postHitService.increaseHitCount(user, "free", freeId);

        return qFreeRepository.getFreePost(freeId);
    }

    /**
     * 게시글 작성
     * 길이 검증 -> 저장
     * @param user                 작성을 요청한 user
     * @param createFreeRequestDto 게시글 내용이 담긴 dto
     * @return 작성 완료한 게시글의 Id를 담은 map
     */
    @Transactional
    public Map<String, Long> createFreePost(User user, CreateFreeRequestDto createFreeRequestDto) {
        String description = communityUtilService.validatePostLength(createFreeRequestDto.getContent());

        return Map.of("freeId", communityUtilService.saveFree(user, createFreeRequestDto, description).getFreeId());
    }

    /**
     * 게시글 수정용 단건 조회
     * @param user 조회를 요청한 user
     * @param freeId 조회할 게시글 Id
     * @return 게시글 상세 정보 dto
     */
    public FreeForUpdateDto getFreePostForUpdate(User user, Long freeId) {
        return FreeForUpdateDto.toDto(communityUtilService.validatePostAndWriter(user, freeId));
    }

    /**
     * 게시글 수정
     * 길이 검증 -> 작성자 검증 -> update
     * @param user                 수정을 요청한 user
     * @param updateFreeRequestDto 수정 내용이 담긴 dto
     */
    @Transactional
    public void updateFreePost(User user, UpdateFreeRequestDto updateFreeRequestDto) {
        String description = communityUtilService.validatePostLength(updateFreeRequestDto.getContent());
        communityUtilService.updatePost(user, updateFreeRequestDto, description);
    }

    /**
     * 게시글 삭제
     * 게시글 조회 -> 작성자 검증
     * @param user   삭제를 요청한 user
     * @param freeId 삭제할 게시글 Id
     */
    @Transactional
    public void deleteFreePost(User user, Long freeId) {
        communityUtilService.validatePostAndWriter(user, freeId).delete();
    }

    /**
     * 인기게시글/추천게시글 조회
     *
     * @param category 추천 게시글에서 같은 category를 조회하기 위한 파라미터
     * @return 인기/추천게시글 각 5개
     */
    public HotAndRecommendsResponseDto hotAndRecommends(String category) {

        return HotAndRecommendsResponseDto.toDto(qFreeRepository.getHotPosts(),
                qFreeRepository.getRecommendsPosts(category));
    }

    /**
     * 원댓글 조회
     * @param freeId 조회할 게시글 Id
     * @param cursor 다음부터 넘겨줄 댓글 Id
     * @param limit 넘길 댓글 개수
     * @return 댓글 목록 dto
     */
    public CommentResponseDto getComments(Long freeId, Long cursor, int limit) {
        return qFreeRepository.getComments(freeId, cursor, limit);
    }

    /**
     * 답글 조회
     * @param commentId 원댓글 Id
     * @param type 처음 5개인지, 이후 전부인지
     * @return 답글 목록 dto
     */
    public List<ReCommentResponseDto> getReComments(Long commentId, String type) {
        return qFreeRepository.getReComments(commentId, type);
    }

    /**
     * 댓글 작성
     * @param user 댓글을 작성자
     * @param commentRequestDto 댓글 내용이 담긴 dto
     * @return 댓글 Id, 작성시간을 담는 dto
     */
    @Transactional
    public CommentCreateResponseDto createComment(User user, CommentCreateRequestDto commentRequestDto) {
        validateCommentLength(commentRequestDto.getContent());
        Comment comment = communityUtilService.createComment(user, commentRequestDto);
        noticeService.sendCommentNotice(user.getNickname(), comment);
        return CommentCreateResponseDto.toDto(comment);
    }

    /**
     * 댓글 수정
     * @param user 댓글을 수정하는 작성자
     * @param updateCommentDto 수정할 내용이 담긴 dto
     */
    @Transactional
    public void updateComment(User user, CommentUpdateRequestDto updateCommentDto) {

        communityUtilService.validateCommentAndWriter(user, updateCommentDto.getCommentId())
                .updateComment(updateCommentDto.getContent());
    }

    /**
     * 댓글 삭제
     * @param user 댓글을 삭제할 사용자
     * @param commentId 삭제할 댓글
     */
    @Transactional
    public void deleteComment(User user, Long commentId) {
        communityUtilService.validateCommentAndWriter(user, commentId).delete();
    }
}
