package com.retailsports.user_service.controller;

import com.retailsports.user_service.dto.request.AddressRequest;
import com.retailsports.user_service.dto.response.AddressResponse;
import com.retailsports.user_service.dto.response.ApiResponse;
import com.retailsports.user_service.model.Address.AddressType;
import com.retailsports.user_service.security.SecurityUtils;
import com.retailsports.user_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    /**
     * GET /api/addresses
     * Ottieni tutti gli indirizzi dell'utente corrente con paginazione
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AddressResponse>>> getUserAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Get addresses for user: {} - page: {}, size: {}", userId, page, size);
        
        Page<AddressResponse> addresses = addressService.getUserAddresses(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    /**
     * GET /api/addresses/type/{type}
     * Ottieni indirizzi per tipo (SHIPPING o BILLING)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddressesByType(
            @PathVariable AddressType type) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Get addresses by type: {} for user: {}", type, userId);
        
        List<AddressResponse> addresses = addressService.getUserAddressesByType(userId, type);
        
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    /**
     * GET /api/addresses/default/{type}
     * Ottieni indirizzo default per tipo
     */
    @GetMapping("/default/{type}")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(
            @PathVariable AddressType type) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Get default address of type: {} for user: {}", type, userId);
        
        AddressResponse address = addressService.getDefaultAddress(userId, type);
        
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    /**
     * GET /api/addresses/{id}
     * Ottieni singolo indirizzo per ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Get address: {} for user: {}", id, userId);
        
        AddressResponse address = addressService.getAddressById(userId, id);
        
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    /**
     * POST /api/addresses
     * Crea nuovo indirizzo
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Create new address for user: {}", userId);
        
        AddressResponse address = addressService.createAddress(userId, request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Address created successfully", address));
    }

    /**
     * PUT /api/addresses/{id}
     * Aggiorna indirizzo esistente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Update address: {} for user: {}", id, userId);
        
        AddressResponse address = addressService.updateAddress(userId, id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }

    /**
     * PUT /api/addresses/{id}/set-default
     * Imposta indirizzo come default
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Set default address: {} for user: {}", id, userId);
        
        AddressResponse address = addressService.setDefaultAddress(userId, id);
        
        return ResponseEntity.ok(ApiResponse.success("Default address set successfully", address));
    }

    /**
     * DELETE /api/addresses/{id}
     * Elimina indirizzo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Delete address: {} for user: {}", id, userId);
        
        addressService.deleteAddress(userId, id);
        
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    /**
     * GET /api/addresses/count
     * Conta indirizzi dell'utente
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countUserAddresses() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Count addresses for user: {}", userId);
        
        long count = addressService.countUserAddresses(userId);
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
