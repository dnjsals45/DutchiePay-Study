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
        ReCommentResponseDtoBuilder builder = ReCommentResponseDto.builder();
        if (originUserLeaved) {
            builder.nickname(null)
                    .profileImg(null)
                    .isModified(null)
                    .userState("탈퇴");
        } else {
            builder.nickname(tuple.get(comment.user.nickname))
                    .profileImg(tuple.get(comment.user.profileImg))
                    .isModified(tuple.get(comment.updatedAt).isAfter(tuple.get(comment.createdAt)))
                    .userState("회원");
        }
        if (mentionedUserLeaved) {
            builder.mentionedNickname(null).mentionedUserState("탈퇴");
        } else {
            builder.mentionedNickname(mentionedUser.get(comment.user.nickname)).mentionedUserState("회원");
        }

        return builder.commentId(tuple.get(comment.commentId))
                .contents(tuple.get(comment.contents))
                .createdAt(tuple.get(comment.createdAt)).build();
    }
}