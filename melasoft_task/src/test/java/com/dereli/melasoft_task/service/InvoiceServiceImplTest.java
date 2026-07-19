package com.dereli.melasoft_task.service;

import com.dereli.melasoft_task.client.ViesClient;
import com.dereli.melasoft_task.model.InvoiceModel;
import com.dereli.melasoft_task.service.impl.InvoiceServiceImpl;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import eu.europa.ec.taxud.vies.services.checkvat.types.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private ViesClient viesClient;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private final ObjectFactory factory = new ObjectFactory();

    private InvoiceModel invoice;

    @BeforeEach
    void setUp() {
        invoice = new InvoiceModel(
                "INV-2026-0001",
                "DE",
                "129273398",
                "FR",
                "40303265045"
        );
    }

    @Test
    void shouldValidateBuyerAndSellerWhenBothValid() {

        CheckVatResponse seller = createResponse(
                "DE",
                "129273398",
                true,
                "Seller Ltd",
                "Berlin"
        );

        CheckVatResponse buyer = createResponse(
                "FR",
                "40303265045",
                true,
                "Buyer SA",
                "Paris"
        );

        when(viesClient.checkVat(any()))
                .thenReturn(seller)
                .thenReturn(buyer);

        invoiceService.validateInvoice(invoice);

        verify(viesClient, times(2)).checkVat(any());
    }

    @Test
    void shouldValidateBuyerAndSellerWhenSellerInvalid() {

        CheckVatResponse seller = createResponse(
                "DE",
                "129273398",
                false,
                "Seller Ltd",
                "Berlin"
        );

        CheckVatResponse buyer = createResponse(
                "FR",
                "40303265045",
                true,
                "Buyer SA",
                "Paris"
        );

        when(viesClient.checkVat(any()))
                .thenReturn(seller)
                .thenReturn(buyer);

        invoiceService.validateInvoice(invoice);

        verify(viesClient, times(2)).checkVat(any());
    }

    @Test
    void shouldValidateBuyerAndSellerWhenBuyerInvalid() {

        CheckVatResponse seller = createResponse(
                "DE",
                "129273398",
                true,
                "Seller Ltd",
                "Berlin"
        );

        CheckVatResponse buyer = createResponse(
                "FR",
                "40303265045",
                false,
                "Buyer SA",
                "Paris"
        );

        when(viesClient.checkVat(any()))
                .thenReturn(seller)
                .thenReturn(buyer);

        invoiceService.validateInvoice(invoice);

        verify(viesClient, times(2)).checkVat(any());
    }

    @Test
    void shouldValidateBuyerAndSellerWhenBothInvalid() {

        CheckVatResponse seller = createResponse(
                "DE",
                "129273398",
                false,
                "Seller Ltd",
                "Berlin"
        );

        CheckVatResponse buyer = createResponse(
                "FR",
                "40303265045",
                false,
                "Buyer SA",
                "Paris"
        );

        when(viesClient.checkVat(any()))
                .thenReturn(seller)
                .thenReturn(buyer);

        invoiceService.validateInvoice(invoice);

        verify(viesClient, times(2)).checkVat(any());
    }

    private CheckVatResponse createResponse(
            String country,
            String vat,
            boolean valid,
            String name,
            String address) {

        CheckVatResponse response = new CheckVatResponse();

        response.setCountryCode(country);
        response.setVatNumber(vat);
        response.setValid(valid);

        response.setName(factory.createCheckVatResponseName(name));
        response.setAddress(factory.createCheckVatResponseAddress(address));

        return response;
    }
}
