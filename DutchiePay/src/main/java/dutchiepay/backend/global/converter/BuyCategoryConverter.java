package dutchiepay.backend.global.converter;

import dutchiepay.backend.domain.commerce.BuyCategoryEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BuyCategoryConverter implements AttributeConverter<BuyCategoryEnum, String> {
    @Override
    public String convertToDatabaseColumn(BuyCategoryEnum buyCategoryEnum) {
        return buyCategoryEnum.getCode();
    }

    @Override
    public BuyCategoryEnum convertToEntityAttribute(String dbData) {
        return BuyCategoryEnum.ofCategory(dbData);
    }
}
