package dutchiepay.backend.domain.delivery.service;

import dutchiepay.backend.domain.delivery.dto.*;
import dutchiepay.backend.domain.delivery.exception.DeliveryErrorCode;
import dutchiepay.backend.domain.delivery.exception.DeliveryErrorException;
import dutchiepay.backend.domain.delivery.repository.AddressRepository;
import dutchiepay.backend.domain.delivery.repository.UsersAddressRepository;
import dutchiepay.backend.entity.Address;
import dutchiepay.backend.entity.User;
import dutchiepay.backend.entity.UsersAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final AddressRepository addressRepository;
    private final UsersAddressRepository usersAddressRepository;

    @Transactional(readOnly = true)
    public List<GetMyDeliveryResponseDto> getDelivery(User user) {
        List<Address> addressList = addressRepository.findAllByUser(user);

        return GetMyDeliveryResponseDto.from(addressList);
    }

    @Transactional
    public CreateDeliveryResponseDto addDelivery(User user, CreateDeliveryRequestDto req) {
        if (usersAddressRepository.countByUser(user) >= 5) {
            throw new DeliveryErrorException(DeliveryErrorCode.ADDRESS_COUNT_LIMIT);
        }

        Address newAddress = Address.builder()
                .addressName(req.getAddressName())
                .receiver(req.getName())
                .phone(req.getPhone())
                .addressInfo(req.getAddress())
                .detail(req.getDetail())
                .zipCode(req.getZipCode())
                .isDefault(req.getIsDefault())
                .build();

        if (newAddress.getIsDefault().equals(Boolean.TRUE)) {
            addressRepository.changeIsDefaultTrueToFalse(user);
        }

        addressRepository.save(newAddress);

        UsersAddress usersAddress = UsersAddress.builder()
                .user(user)
                .address(newAddress)
                .build();

        usersAddressRepository.save(usersAddress);

        return CreateDeliveryResponseDto.from(newAddress);
    }

    @Transactional
    public void updateDelivery(User user, ChangeDeliveryRequestDto req) {
        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new DeliveryErrorException(DeliveryErrorCode.INVALID_ADDRESS));

        if (req.getIsDefault().equals(Boolean.TRUE)) {
            addressRepository.changeIsDefaultTrueToFalse(user);
        }

        address.update(req);
        addressRepository.save(address);
    }

    @Transactional
    public void deleteDelivery(User user, DeleteDeliveryRequestDto req) {
        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new DeliveryErrorException(DeliveryErrorCode.INVALID_ADDRESS));

        if (address.getIsDefault().equals(Boolean.TRUE)) {
            addressRepository.changeOldestAddressToDefault(user);
        }

        usersAddressRepository.deleteByUserAndAddress(user, address);
        addressRepository.delete(address);
    }
}
