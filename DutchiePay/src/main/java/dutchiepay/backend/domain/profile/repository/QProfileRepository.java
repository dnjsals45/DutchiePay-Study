package dutchiepay.backend.domain.profile.repository;

import dutchiepay.backend.domain.profile.dto.GetMyLikesResponseDto;
import dutchiepay.backend.domain.profile.dto.MyGoodsResponseDto;
import dutchiepay.backend.domain.profile.dto.MyPostsResponseDto;
import dutchiepay.backend.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProfileRepository {
    List<GetMyLikesResponseDto> getMyLike(User user);

    MyGoodsResponseDto getMyGoods(User user, String filter, Pageable pageable);

    List<MyPostsResponseDto> getMyPosts(User user, PageRequest pageable);

    List<MyPostsResponseDto> getMyCommentsPosts(User user, PageRequest pageable);
}
