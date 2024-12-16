package dutchiepay.backend.domain.search.service;

import dutchiepay.backend.domain.commerce.repository.BuyRepository;
import dutchiepay.backend.domain.commerce.repository.QBuyRepository;
import dutchiepay.backend.domain.commerce.repository.QBuyRepositoryImpl;
import dutchiepay.backend.domain.search.dto.CommerceSearchResponseDto;
import dutchiepay.backend.domain.search.dto.DictionaryResponseDto;
import dutchiepay.backend.domain.search.repository.QSearchRepository;
import dutchiepay.backend.entity.Buy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final QSearchRepository qSearchRepository;
    private final QBuyRepositoryImpl qBuyRepository;

    public DictionaryResponseDto getSearchDictionary() {
        Set<String> tags = qBuyRepository.findAllTags().stream()
                .flatMap(s -> Arrays.stream(s.split(", ")))
                .collect(Collectors.toSet());
        return DictionaryResponseDto.builder().tags(tags).build();
    }

    public CommerceSearchResponseDto commerceSearch(String keyword, String filter, int end, Long cursor, int limit) {

        return null;
    }
}
