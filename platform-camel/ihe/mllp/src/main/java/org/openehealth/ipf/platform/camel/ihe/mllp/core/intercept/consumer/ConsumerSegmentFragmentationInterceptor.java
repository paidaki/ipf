/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2MarshalUtils;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.FragmentationUtils;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpEndpoint;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.AbstractMllpInterceptor;


/**
 * Interceptor that reads in the HL7 request String using the configured 
 * character set and handles segment fragmentation (\rADD|...). 
 * @author Dmytro Rud
 */
public class ConsumerSegmentFragmentationInterceptor extends AbstractMllpInterceptor {

    public ConsumerSegmentFragmentationInterceptor(MllpEndpoint endpoint, Processor wrappedProcessor) {
        super(endpoint, wrappedProcessor);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        boolean supportSegmentFragmentation = getMllpEndpoint().isSupportSegmentFragmentation();
        int segmentFragmentationThreshold = getMllpEndpoint().getSegmentFragmentationThreshold();
        
        // read in the request
        Message message = exchange.getIn();
        message.setBody(Hl7v2MarshalUtils.convertBodyToString(
                message,
                getMllpEndpoint().getConfiguration().getCharsetName(),
                supportSegmentFragmentation));
        
        // run the route
        getWrappedProcessor().process(exchange);
        
        // preprocess output
        if (supportSegmentFragmentation && (segmentFragmentationThreshold >= 5)) {
            message = Exchanges.resultMessage(exchange);
            String s = message.getBody(String.class);
            s = FragmentationUtils.ensureMaximalSegmentsLength(s, segmentFragmentationThreshold);
            message.setBody(s);
        }

    }
}