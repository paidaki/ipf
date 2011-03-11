/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.xca.iti38;

import org.openehealth.ipf.commons.ihe.atna.AuditorManager;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.iti18.Iti18AuditDataset;

/**
 * Server audit strategy for ITI-38.
 * @author Dmytro Rud
 */
public class Iti38ServerAuditStrategy extends Iti38AuditStrategy {

    private static final String[] NECESSARY_AUDIT_FIELDS = new String[] {
        "EventOutcomeCode",
        "ClientIpAddress",
        "ServiceEndpointUrl",
        "QueryUuid",
        "RequestPayload",
        "HomeCommunityId"
        /*"PatientId"*/};

    
    /**
     * Constructs the audit strategy.
     * @param allowIncompleteAudit
     *      whether this strategy should allow incomplete audit records
     *      (parameter initially configurable via endpoint URL).
     */
    public Iti38ServerAuditStrategy(boolean allowIncompleteAudit) {
        super(true, allowIncompleteAudit);
    }

    @Override
    public void doAudit(WsAuditDataset auditDataset) {
        Iti18AuditDataset xcaAuditDataset = (Iti18AuditDataset) auditDataset;
        AuditorManager.getXCARespondingGatewayAuditor().auditCrossGatewayQueryEvent(
                xcaAuditDataset.getEventOutcomeCode(),
                xcaAuditDataset.getUserId(),
                xcaAuditDataset.getUserName(),
                xcaAuditDataset.getClientIpAddress(),
                xcaAuditDataset.getServiceEndpointUrl(),
                xcaAuditDataset.getQueryUuid(),
                xcaAuditDataset.getRequestPayload(),
                xcaAuditDataset.getHomeCommunityId(),
                xcaAuditDataset.getPatientId());
    }

    @Override
    public String[] getNecessaryAuditFieldNames() {
        return NECESSARY_AUDIT_FIELDS;
    }
}