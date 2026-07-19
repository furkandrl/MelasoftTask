package com.dereli.melasoft_task.client.impl;

import com.dereli.melasoft_task.client.ViesClient;
import com.dereli.melasoft_task.enums.ViesFaultEnum;
import com.dereli.melasoft_task.exception.SchemaValidationException;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatPortType;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatService;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVat;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import eu.europa.ec.taxud.vies.services.checkvat.types.ObjectFactory;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.apache.cxf.endpoint.Client;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceException;
import org.xml.sax.SAXException;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class ViesClientImpl implements ViesClient {
    private static final Logger log = LoggerFactory.getLogger(ViesClientImpl.class);


    @Value("${vies.retry.max-attempts}")
    private int maxAttempts;

    @Value("${vies.retry.delay-ms}")
    private long retryDelayMs;

    @Value("${vies.connection-timeout-ms}")
    private long connectionTimeoutMs;

    @Value("${vies.receive-timeout-ms}")
    private long receiveTimeoutMs;

    private final CheckVatPortType port;
    ObjectFactory factory = new ObjectFactory();

    public ViesClientImpl() {
        CheckVatService service = new CheckVatService();
        this.port = service.getCheckVatPort();
        Client client = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(connectionTimeoutMs);
        policy.setReceiveTimeout(receiveTimeoutMs);

        conduit.setClient(policy);

        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(
                Message.SCHEMA_VALIDATION_ENABLED,
                Boolean.TRUE);
    }



    public CheckVatResponse checkVat(CheckVat request) throws RuntimeException {

        CheckVatResponse response = new CheckVatResponse();
        int attempt = 0;

        while (true) {
            try {
                Holder<String> cc = new Holder<>(request.getCountryCode());
                Holder<String> vn = new Holder<>(request.getVatNumber());
                Holder<XMLGregorianCalendar> requestDate = new Holder<>();
                Holder<Boolean> valid = new Holder<>();
                Holder<String> name = new Holder<>();
                Holder<String> address = new Holder<>();

                port.checkVat(cc, vn, requestDate, valid, name, address);

                response.setCountryCode(cc.value);
                response.setVatNumber(vn.value);
                response.setRequestDate(requestDate.value);
                response.setValid(valid.value);
                response.setName(factory.createCheckVatResponseName(name.value));
                response.setAddress(factory.createCheckVatResponseAddress(address.value));
                return response;
            } catch (SOAPFaultException exc) {
                String faultCode = exc.getFault().getFaultString();
                if (faultCode.equals(ViesFaultEnum.INVALID_INPUT.name())) {
                    throw exc;
                }
                if (isRetryableFault(faultCode)) {

                    attempt++;

                    log.warn("VIES returned '{}'. Retry {}/{}.",
                            faultCode, attempt, maxAttempts);

                    if (attempt >= maxAttempts) {
                        throw exc;
                    }

                    sleep();

                    continue;
                }

                throw exc;

            } catch (WebServiceException exc) {

                Throwable cause = exc;
                while (cause != null) {

                    if (cause instanceof SAXException) {
                        throw new SchemaValidationException(
                                "SOAP response failed XSD validation",
                                exc.getCause()
                        );
                    }

                    cause = cause.getCause();
                }

                throw exc;
            }

        }
    }


    private boolean isRetryableFault(String fault) {
        try {
            ViesFaultEnum.valueOf(fault);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void sleep() {

        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }

}
