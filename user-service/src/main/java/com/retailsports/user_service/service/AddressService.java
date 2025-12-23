package com.retailsports.user_service.service;

import com.retailsports.user_service.dto.request.AddressRequest;
import com.retailsports.user_service.dto.response.AddressResponse;
import com.retailsports.user_service.exception.BadRequestException;
import com.retailsports.user_service.exception.ResourceNotFoundException;
import com.retailsports.user_service.model.Address;
import com.retailsports.user_service.model.Address.AddressType;
import com.retailsports.user_service.model.User;
import com.retailsports.user_service.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    /**
     * Ottieni tutti gli indirizzi di un utente con paginazione
     */
    @Transactional(readOnly = true)
    public Page<AddressResponse> getUserAddresses(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        List<Address> addresses = addressRepository.findByUserId(userId);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), addresses.size());
        
        List<AddressResponse> addressResponses = addresses.subList(start, end)
            .stream()
            .map(this::convertToAddressResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            addressResponses,
            pageable,
            addresses.size()
        );
    }

    /**
     * Ottieni indirizzi per tipo (SHIPPING o BILLING)
     */
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddressesByType(Long userId, AddressType type) {
        List<Address> addresses = addressRepository.findByUserIdAndType(userId, type);
        
        return addresses.stream()
            .map(this::convertToAddressResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni indirizzo default dell'utente
     */
    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddress(Long userId, AddressType type) {
        Address address = addressRepository.findDefaultAddressByUserIdAndType(userId, type)
            .orElseThrow(() -> new ResourceNotFoundException("No default address found for type: " + type));
        
        return convertToAddressResponse(address);
    }

    /**
     * Ottieni singolo indirizzo per ID
     */
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        // Verifica che l'indirizzo appartenga all'utente
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }
        
        return convertToAddressResponse(address);
    }

    /**
     * Crea nuovo indirizzo
     */
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        log.info("Creating new address for user: {}", userId);

        User user = userService.findById(userId);

        // Se è il primo indirizzo o è marcato come default, rimuovi altri default dello stesso tipo
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.removeDefaultFlagForUserAndType(userId, request.getType());
        }

        // Se è il primo indirizzo di questo tipo, impostalo come default
        List<Address> existingAddresses = addressRepository.findByUserIdAndType(userId, request.getType());
        boolean isFirstOfType = existingAddresses.isEmpty();

        Address address = Address.builder()
            .user(user)
            .type(request.getType())
            .addressLine1(request.getAddressLine1())
            .addressLine2(request.getAddressLine2())
            .city(request.getCity())
            .state(request.getState())
            .postalCode(request.getPostalCode())
            .country(request.getCountry())
            .isDefault(isFirstOfType || (request.getIsDefault() != null && request.getIsDefault()))
            .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully: {}", savedAddress.getId());

        return convertToAddressResponse(savedAddress);
    }

    /**
     * Aggiorna indirizzo esistente
     */
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        log.info("Updating address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Verifica ownership
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }

        // Se si sta cambiando il tipo, gestisci il flag default
        if (request.getType() != null && !request.getType().equals(address.getType())) {
            address.setType(request.getType());
            address.setIsDefault(false); // Reset default se cambia tipo
        }

        // Aggiorna campi
        if (request.getAddressLine1() != null) {
            address.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            address.setAddressLine2(request.getAddressLine2());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getState() != null) {
            address.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully: {}", addressId);

        return convertToAddressResponse(updatedAddress);
    }

    /**
     * Imposta indirizzo come default
     */
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        log.info("Setting default address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Verifica ownership
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }

        // Rimuovi flag default da altri indirizzi dello stesso tipo
        addressRepository.removeDefaultFlagForUserAndType(userId, address.getType());

        // Imposta questo come default
        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);

        log.info("Default address set: {}", addressId);

        return convertToAddressResponse(updatedAddress);
    }

    /**
     * Elimina indirizzo
     */
    public void deleteAddress(Long userId, Long addressId) {
        log.info("Deleting address: {} for user: {}", addressId, userId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Verifica ownership
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }

        // Se era default, imposta il primo disponibile come nuovo default
        if (address.getIsDefault()) {
            addressRepository.delete(address);
            
            List<Address> remainingAddresses = addressRepository.findByUserIdAndType(userId, address.getType());
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
                log.info("New default address set: {}", newDefault.getId());
            }
        } else {
            addressRepository.delete(address);
        }

        log.info("Address deleted successfully: {}", addressId);
    }

    /**
     * Conta indirizzi di un utente
     */
    @Transactional(readOnly = true)
    public long countUserAddresses(Long userId) {
        return addressRepository.countByUserId(userId);
    }

    /**
     * Verifica se l'utente ha almeno un indirizzo
     */
    @Transactional(readOnly = true)
    public boolean hasAddresses(Long userId) {
        return countUserAddresses(userId) > 0;
    }

    // ====================================
    // UTILITY METHODS
    // ====================================

    /**
     * Converte Address entity in AddressResponse DTO
     */
    private AddressResponse convertToAddressResponse(Address address) {
        return AddressResponse.builder()
            .id(address.getId())
            .type(address.getType())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .state(address.getState())
            .postalCode(address.getPostalCode())
            .country(address.getCountry())
            .isDefault(address.getIsDefault())
            .createdAt(address.getCreatedAt())
            .updatedAt(address.getUpdatedAt())
            .build();
    }
}