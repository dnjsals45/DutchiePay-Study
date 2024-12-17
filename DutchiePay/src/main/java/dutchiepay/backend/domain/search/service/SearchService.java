package dutchiepay.backend.domain.search.service;

import dutchiepay.backend.domain.commerce.dto.GetBuyListResponseDto;
import dutchiepay.backend.domain.commerce.repository.QBuyRepositoryImpl;
import dutchiepay.backend.domain.search.dto.DictionaryResponseDto;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final QBuyRepositoryImpl qBuyRepository;

    /**
     * Buy 엔티티의 모든 tags를 리턴하는 메서드
     * @return tags가 들어있는 dto
     */
    public DictionaryResponseDto getSearchDictionary() {
        Set<String> tags = qBuyRepository.findAllTags().stream()
                .flatMap(s -> Arrays.stream(s.split(", ")))
                .collect(Collectors.toSet());
        return DictionaryResponseDto.builder().tags(tags).build();
    }

    /**
     * 공동구매 검색 결과 반환
     * QBuyRepositoryImpl의 getBuyList 사용
     * @param user 회원이면 user 정보, 비회원이면 null
     * @param keyword 검색할 단어
     * @param filter 필터링 조건
     * @param end 마감된 공구 포함여부
     * @param cursor 다음으로 검색할 Id
     * @param limit 반환할 개수
     * @return 검색 결과가 담긴 dto
     */
    public GetBuyListResponseDto commerceSearch(User user, String filter, String keyword, int end, Long cursor, int limit) {
        System.out.println("CommerceSearch");
        System.out.println(keyword);
        System.out.println(filter);

        return qBuyRepository.getBuyList(user, filter, null, keyword, end, cursor, limit);
    }
}
