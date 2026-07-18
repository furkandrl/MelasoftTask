package com.dereli.melasoft_task.mapper;

import com.dereli.melasoft_task.dto.VatValidationResult;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
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
                response.getRequestDate().toGregorianCalendar().toZonedDateTime().toString()
        );
    }
}
