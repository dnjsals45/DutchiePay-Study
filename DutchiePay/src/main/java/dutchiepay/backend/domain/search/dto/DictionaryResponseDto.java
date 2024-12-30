package dutchiepay.backend.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class DictionaryResponseDto {
    private Set<String> tags;
}
