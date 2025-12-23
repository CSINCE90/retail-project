package com.retailsports.user_service.dto.request;

import com.retailsports.user_service.model.Address.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotNull(message = "Address type is required")
    private AddressType type;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must be less than 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must be less than 255 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @Size(max = 100, message = "State must be less than 100 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters (ISO 3166-1 alpha-2)")
    private String country;

    private Boolean isDefault;
}
