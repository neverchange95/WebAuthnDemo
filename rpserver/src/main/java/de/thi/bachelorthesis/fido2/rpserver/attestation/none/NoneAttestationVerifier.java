package de.thi.bachelorthesis.fido2.rpserver.attestation.none;

import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerifier;
import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NoneAttestationVerifier implements AttestationVerifier {

    @Override
    public AttestationStatementFormatIdentifier getIdentifier() {
        return AttestationStatementFormatIdentifier.NONE;
    }

    @Override
    public AttestationVerificationResult verify(AttestationStatement attestationStatement, AuthenticatorData authenticatorData, byte[] clientDataHash) {
        return AttestationVerificationResult
                .builder()
                .success(true)
                .type(AttestationType.NONE)
                .trustPath(new ArrayList<>())
                .format(AttestationStatementFormatIdentifier.NONE)
                .build();

    }
}