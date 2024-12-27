package dutchiepay.backend.domain.community.dto;

import com.querydsl.core.Tuple;
import dutchiepay.backend.entity.Comment;
import dutchiepay.backend.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static dutchiepay.backend.entity.QComment.comment;

@Getter
@Builder
public class ReCommentResponseDto {

    private Long commentId;
    private String mentionedNickname;
    private String nickname;
    private String profileImg;
    private String contents;
    private LocalDateTime createdAt;
    private Boolean isModified;
    private String userState;
    private String mentionedUserState;

    public static ReCommentResponseDto toDto(Tuple tuple, Tuple mentionedUser) {
        boolean originUserLeaved = tuple.get(comment.user.state) != 0;
        boolean mentionedUserLeaved = mentionedUser.get(comment.user.state) != 0;

        return ReCommentResponseDto.builder()
                .commentId(tuple.get(comment.commentId))
                .mentionedNickname(mentionedUserLeaved ? null : mentionedUser.get(comment.user.nickname))
                .nickname(originUserLeaved ? null : tuple.get(comment.user.nickname))
                .profileImg(originUserLeaved ? null : tuple.get(comment.user.profileImg))
                .contents(tuple.get(comment.contents))
                .createdAt(tuple.get(comment.createdAt))
                .isModified(originUserLeaved ? null : tuple.get(comment.updatedAt).isAfter(tuple.get(comment.createdAt)))
                .userState(originUserLeaved ? "탈퇴" : "회원")
                .mentionedUserState(mentionedUserLeaved ? "탈퇴" : "회원")
                .build();
    }
}