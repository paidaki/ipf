/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.fhir.iti81;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.openehealth.ipf.commons.ihe.fhir.FhirTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.fhir.FhirTransactionValidator;
import org.openehealth.ipf.commons.ihe.fhir.audit.FhirQueryAuditDataset;

/**
 * Standard Configuration for Iti81Component
 *
 * @author Christian Ohr
 * @since 3.6
 */
public class Iti81TransactionConfiguration extends FhirTransactionConfiguration<FhirQueryAuditDataset> {

    public Iti81TransactionConfiguration() {
        super("atna-iti81",
                "Retrieve ATNA Audit Event",
                true,
                new Iti81AuditStrategy(false),
                new Iti81AuditStrategy(true),
                FhirVersionEnum.R4,
                new Iti81ResourceProvider(),       // Consumer side. accept audit searches
                new Iti81ClientRequestFactory(),
                FhirTransactionValidator.NO_VALIDATION);
    }
}
