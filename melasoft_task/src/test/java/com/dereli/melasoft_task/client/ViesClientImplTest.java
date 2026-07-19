package com.dereli.melasoft_task.client;

import com.dereli.melasoft_task.client.impl.ViesClientImpl;
import com.dereli.melasoft_task.config.ViesClientProperties;
import com.dereli.melasoft_task.exception.SchemaValidationException;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatPortType;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVat;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;
import jakarta.xml.ws.WebServiceException;

import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViesClientImplTest {

    @Mock
    private CheckVatPortType port;

    private ViesClientImpl viesClient;

    @BeforeEach
    void setUp() {

        ViesClientProperties properties = new ViesClientProperties();
        properties.setConnectionTimeoutMs(5000);
        properties.setReceiveTimeoutMs(10000);

        ViesClientProperties.Retry retry = new ViesClientProperties.Retry();
        properties.getRetry().setMaxAttempts(3);
        properties.getRetry().setDelayMs(1);


        viesClient = new ViesClientImpl(properties, port);
    }

    @Test
    void shouldReturnValidResponse() {

        doAnswer(invocation -> {

            Holder<String> cc = invocation.getArgument(0);
            Holder<String> vn = invocation.getArgument(1);
            Holder<XMLGregorianCalendar> requestDate = invocation.getArgument(2);
            Holder<Boolean> valid = invocation.getArgument(3);
            Holder<String> name = invocation.getArgument(4);
            Holder<String> address = invocation.getArgument(5);

            cc.value = "DE";
            vn.value = "123456789";
            valid.value = true;
            name.value = "OpenAI GmbH";
            address.value = "Berlin";

            return null;

        }).when(port).checkVat(any(), any(), any(), any(), any(), any());

        CheckVat request = new CheckVat();
        request.setCountryCode("DE");
        request.setVatNumber("123456789");

        CheckVatResponse response = viesClient.checkVat(request);

        assertTrue(response.isValid());
        assertEquals("DE", response.getCountryCode());
        assertEquals("123456789", response.getVatNumber());
        assertEquals("OpenAI GmbH", response.getName().getValue());
        assertEquals("Berlin", response.getAddress().getValue());

        verify(port, times(1))
                .checkVat(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowImmediatelyForInvalidInput() throws Exception {

        SOAPFault fault = SOAPFactory.newInstance().createFault();
        fault.setFaultString("INVALID_INPUT");

        doThrow(new SOAPFaultException(fault))
                .when(port)
                .checkVat(any(), any(), any(), any(), any(), any());

        CheckVat request = new CheckVat();

        assertThrows(
                SOAPFaultException.class,
                () -> viesClient.checkVat(request));

        verify(port, times(1))
                .checkVat(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldRetryAndEventuallySucceed() throws Exception {

        SOAPFault fault = SOAPFactory.newInstance().createFault();
        fault.setFaultString("TIMEOUT");

        doThrow(new SOAPFaultException(fault))
                .doAnswer(invocation -> {

                    Holder<Boolean> valid = invocation.getArgument(3);
                    valid.value = true;

                    return null;

                }).when(port)
                .checkVat(any(), any(), any(), any(), any(), any());

        CheckVatResponse response = viesClient.checkVat(new CheckVat());

        assertTrue(response.isValid());

        verify(port, times(2))
                .checkVat(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowWhenRetryLimitExceeded() throws Exception {

        SOAPFault fault = SOAPFactory.newInstance().createFault();
        fault.setFaultString("TIMEOUT");

        doThrow(new SOAPFaultException(fault))
                .when(port)
                .checkVat(any(), any(), any(), any(), any(), any());

        assertThrows(
                SOAPFaultException.class,
                () -> viesClient.checkVat(new CheckVat()));

        verify(port, times(3))
                .checkVat(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowSchemaValidationException() throws Exception {

        WebServiceException exception =
                new WebServiceException(new SAXException("Invalid XML"));

        doThrow(exception)
                .when(port)
                .checkVat(any(), any(), any(), any(), any(), any());

        assertThrows(
                SchemaValidationException.class,
                () -> viesClient.checkVat(new CheckVat()));

        verify(port, times(1))
                .checkVat(any(), any(), any(), any(), any(), any());
    }
}