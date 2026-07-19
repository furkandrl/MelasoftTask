package com.dereli.melasoft_task.config;

import eu.europa.ec.taxud.vies.services.checkvat.CheckVatPortType;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatService;
import jakarta.xml.ws.BindingProvider;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViesClientConfig {
    @Bean
    public CheckVatPortType checkVatPort(ViesClientProperties properties) {

        CheckVatService service = new CheckVatService();
        CheckVatPortType port = service.getCheckVatPort();

        Client client = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(properties.getConnectionTimeoutMs());
        policy.setReceiveTimeout(properties.getReceiveTimeoutMs());

        conduit.setClient(policy);

        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(
                Message.SCHEMA_VALIDATION_ENABLED,
                Boolean.TRUE);

        return port;
    }
}
