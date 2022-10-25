package de.thi.bachelorthesis.fido2.rpserver.attestation;

import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import lombok.Builder;
import lombok.Data;

import java.security.cert.Certificate;
import java.util.List;

@Data
@Builder
public class AttestationVerificationResult {
    private boolean success;
    private AttestationType type;
    private List<Certificate> trustPath;
    private byte[] ecdaaKeyId;
    private AttestationStatementFormatIdentifier format;
}
