package dutchiepay.backend.entity;

import dutchiepay.backend.domain.delivery.dto.ChangeDeliveryRequestDto;
import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Address")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false, length = 11)
    private String phone;

    @Column(nullable = false)
    private String addressInfo;

    private String detail;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private Boolean isDefault;

    public void update(ChangeDeliveryRequestDto req) {
        this.addressName = req.getAddressName();
        this.receiver = req.getName();
        this.phone = req.getPhone();
        this.addressInfo = req.getAddress();
        this.detail = req.getDetail();
        this.zipCode = req.getZipCode();
        this.isDefault = req.getIsDefault();
    }

    public void changeDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
