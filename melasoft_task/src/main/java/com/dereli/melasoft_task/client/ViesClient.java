package com.dereli.melasoft_task.client;

public interface ViesClient {
    VatValidationResult checkVat(String countryCode, String vatNumber);
}
