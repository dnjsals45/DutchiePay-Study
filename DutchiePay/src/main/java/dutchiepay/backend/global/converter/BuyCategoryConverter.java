package dutchiepay.backend.global.converter;

import dutchiepay.backend.domain.commerce.BuyCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BuyCategoryConverter implements AttributeConverter<BuyCategory, String> {
    @Override
    public String convertToDatabaseColumn(BuyCategory buyCategory) {
        return buyCategory.getCode();
    }

    @Override
    public BuyCategory convertToEntityAttribute(String dbData) {
        return BuyCategory.ofCategory(dbData);
    }
}
