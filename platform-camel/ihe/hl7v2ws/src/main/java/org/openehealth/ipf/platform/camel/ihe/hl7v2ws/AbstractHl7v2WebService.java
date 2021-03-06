/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.hl7v2ws;

import java.io.IOException;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.apache.camel.Exchange;
import org.openehealth.ipf.commons.ihe.hl7v2.Hl7v2TransactionConfiguration;
import org.openehealth.ipf.commons.ihe.hl7v2.NakFactory;
import org.openehealth.ipf.modules.hl7.message.MessageUtils;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2ConfigurationHolder;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2MarshalUtils;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.openehealth.ipf.commons.ihe.hl7v2ws.Utils.render;
import static org.openehealth.ipf.platform.camel.core.util.Exchanges.resultMessage;

/**
 * Generic implementation of an HL7v2-based Web Service.
 * 
 * @author Dmytro Rud
 * @author Mitko Kolev
 * @author Stefan Ivanov
 */
public abstract class AbstractHl7v2WebService extends AbstractWebService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHl7v2WebService.class);

    private Hl7v2TransactionConfiguration config = null;
    private NakFactory nakFactory = null;


    /**
     * Configures HL7v2-related parameters of this Web Service.
     * This method is supposed to be called only once, directly after
     * an instance of this class has been created.  In other words,
     * this is a kind of "post-constructor".
     *
     * @param configurationHolder
     *      source of HL7v2-related configuration parameters.
     */
    void setHl7v2Configuration(Hl7v2ConfigurationHolder configurationHolder) {
        if ((this.config != null) || (this.nakFactory != null)) {
            throw new IllegalStateException("multiple calls to this method are not allowed");
        }

        this.config = configurationHolder.getHl7v2TransactionConfiguration();
        this.nakFactory = configurationHolder.getNakFactory();
    }


    protected String doProcess(String request) {
        // parse request
        Message msg;
        try {
            msg = config.getParser().parse(trimToEmpty(request).replaceAll("\n", "\r\n"));
            config.checkRequestAcceptance(msg);
        } catch (HL7Exception e) {
            LOG.error(formatErrMsg("Request not acceptable"), e);
            return render(nakFactory.createDefaultNak(e));
        }

        Message originalRequest = MessageUtils.copy(msg);

        // play the route, handle its outcomes and check response acceptance
        try {
            Exchange exchange = super.process(msg);
            Exception exception = Exchanges.extractException(exchange);
            if (exception != null) {
                throw exception;
            }

            // check response existence and acceptance
            if ((msg = Hl7v2MarshalUtils.extractHapiMessage(
                    resultMessage(exchange),
                    exchange.getProperty(Exchange.CHARSET_NAME, String.class),
                    config.getParser())) != null) {
                config.checkResponseAcceptance(msg);
                return render(msg);
            } else {
                throw new Exception("Could not extract HAPI message object from exchange");
            }

        } catch (Exception e) {
            LOG.error(formatErrMsg("Message processing failed, response missing or not acceptable"), e);
            try {
                return render(nakFactory.createNak(originalRequest, e));
            } catch (HL7Exception e1) {
                return render(MessageUtils.defaultNak(e1, AcknowledgmentCode.AE, msg.getVersion()));
            } catch (IOException e2) {
                return render(MessageUtils.defaultNak(new HL7Exception(e2), AcknowledgmentCode.AE, msg.getVersion()));
            }
        }
    }


    /**
     * Creates a message from the input <code>text</code >with prefix the sending
     * application in the transaction configuration.
     * 
     * @param text
     *            input text used for logging
     * @return a String with prefix the sending application
     */
    protected String formatErrMsg(String text) {
        return config.getSendingApplication() + ": " + text;
    }
    
}
