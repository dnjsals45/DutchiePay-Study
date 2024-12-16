package dutchiepay.backend.domain.search.controller;

import dutchiepay.backend.domain.search.dto.CommerceSearchResponseDto;
import dutchiepay.backend.domain.search.dto.DictionaryResponseDto;
import dutchiepay.backend.domain.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색 사전")
    @GetMapping
    public ResponseEntity<DictionaryResponseDto> getSearchDictionary() {
        return ResponseEntity.ok(searchService.getSearchDictionary());
    }

    @Operation(summary = "공동구매 검색")
    @GetMapping("/commerce")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommerceSearchResponseDto> commerceSearch(@RequestParam("keyword") String keyword,
                                                                    @RequestParam("filter") String filter,
                                                                    @RequestParam("end") int end,
                                                                    @RequestParam(value="cursor", required = false) Long cursor,
                                                                    @RequestParam("limit") int limit) {
        return ResponseEntity.ok(searchService.commerceSearch(keyword, filter, end, cursor, limit));
    }

}
