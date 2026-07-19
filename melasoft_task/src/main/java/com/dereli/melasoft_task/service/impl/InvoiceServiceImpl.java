package com.dereli.melasoft_task.service.impl;

import com.dereli.melasoft_task.client.ViesClient;
import com.dereli.melasoft_task.model.InvoiceModel;
import com.dereli.melasoft_task.service.InvoiceService;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVat;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final ViesClient viesClient;

    public InvoiceServiceImpl(ViesClient viesClient) {
        this.viesClient = viesClient;
    }


    public void validateInvoice(InvoiceModel invoice) {
        CheckVatResponse sellerResponse = validateVat(
                "Seller",
                invoice.getSellerCountryCode(),
                invoice.getSellerVatNumber());

        CheckVatResponse buyerResponse = validateVat(
                "Buyer",
                invoice.getBuyerCountryCode(),
                invoice.getBuyerVatNumber());

        if (sellerResponse.isValid() && buyerResponse.isValid()) {
            log.info("****Invoice can be issued.");

            log.info("Seller: {}", sellerResponse.getName().getValue());
            log.info("Address: {}", sellerResponse.getAddress().getValue());

            log.info("Buyer: {}", buyerResponse.getName().getValue());
            log.info("Address: {}", buyerResponse.getAddress().getValue());

        } else if (!sellerResponse.isValid()) {
            log.info("***Invoice cannot be issued: Seller VAT number is invalid.");

        } else {
            log.info("***Invoice cannot be issued: Buyer VAT number is invalid.");
        }
    }

    private CheckVatResponse validateVat(String party,
                                         String countryCode,
                                         String vatNumber) {

        CheckVat request = new CheckVat();
        request.setCountryCode(countryCode);
        request.setVatNumber(vatNumber);


        CheckVatResponse response = viesClient.checkVat(request);

        log.info("{} VAT validation:", party);
        log.info("Country: {}", response.getCountryCode());
        log.info("VAT Number: {}", response.getVatNumber());
        log.info("Valid: {}", response.isValid());

        return response;
    }
}
