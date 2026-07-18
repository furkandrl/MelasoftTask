package com.dereli.melasoft_task.mapper;

import org.springframework.stereotype.Component;

@Component
public class VatValidationMapper {
    public VatValidationResult toResult(CheckVatResponse response) {
        return new VatValidationResult(
                response.getCountryCode(),
                response.getVatNumber(),
                response.isValid(),
                response.getName() != null ? response.getName().getValue() : null,
                response.getAddress() != null ? response.getAddress().getValue() : null,
                response.getRequestDate()
        );
    }
}
