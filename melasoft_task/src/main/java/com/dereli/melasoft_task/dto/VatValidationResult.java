package com.dereli.melasoft_task.dto;

public record VatValidationResult(
        String countryCode,
        String vatNumber,
        boolean valid,
        String name,
        String address,
        String requestDate
) {}

