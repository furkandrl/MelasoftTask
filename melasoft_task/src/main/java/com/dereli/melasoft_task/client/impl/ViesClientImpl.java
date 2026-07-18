package com.dereli.melasoft_task.client.impl;

import com.dereli.melasoft_task.client.ViesClient;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatPortType;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatService;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVat;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import eu.europa.ec.taxud.vies.services.checkvat.types.ObjectFactory;
import jakarta.xml.ws.Holder;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class ViesClientImpl implements ViesClient {
    private final CheckVatPortType port;
    ObjectFactory factory = new ObjectFactory();

    public ViesClientImpl() {
        CheckVatService service = new CheckVatService();
        this.port = service.getCheckVatPort();
    }

    public CheckVatResponse checkVat(CheckVat request) {

        Holder<String> cc = new Holder<>(request.getCountryCode());
        Holder<String> vn = new Holder<>(request.getVatNumber());
        Holder<XMLGregorianCalendar> requestDate = new Holder<>();
        Holder<Boolean> valid = new Holder<>();
        Holder<String> name = new Holder<>();
        Holder<String> address = new Holder<>();

        port.checkVat(cc, vn, requestDate, valid, name, address);

        CheckVatResponse response = new CheckVatResponse();
        response.setCountryCode(cc.value);
        response.setVatNumber(vn.value);
        response.setRequestDate(requestDate.value);
        response.setValid(valid.value);
        response.setName(factory.createCheckVatResponseName(name.value));
        response.setAddress(factory.createCheckVatResponseAddress(address.value));
        return response;
    }

}
