package dutchiepay.backend.domain.delivery.service;

import dutchiepay.backend.domain.delivery.dto.GetMyDeliveryResponseDto;
import dutchiepay.backend.domain.profile.repository.AddressRepository;
import dutchiepay.backend.entity.Address;
import dutchiepay.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final AddressRepository addressRepository;

    public List<GetMyDeliveryResponseDto> getDelivery(User user) {
        List<Address> addressList = addressRepository.findAllByUser(user);

        return GetMyDeliveryResponseDto.from(addressList);
    }
}
