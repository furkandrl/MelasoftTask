package com.dereli.melasoft_task.client;

import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVat;
import eu.europa.ec.taxud.vies.services.checkvat.types.CheckVatResponse;
import org.xml.sax.SAXException;

public interface ViesClient {
    CheckVatResponse checkVat(CheckVat request)throws RuntimeException;
}
