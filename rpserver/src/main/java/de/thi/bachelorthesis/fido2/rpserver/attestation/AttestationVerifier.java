package de.thi.bachelorthesis.fido2.rpserver.attestation;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorData;

public interface AttestationVerifier {
    AttestationStatementFormatIdentifier getIdentifier();

    AttestationVerificationResult verify(AttestationStatement attestationStatement, AuthenticatorData authenticatorData, byte[] clientDataHash);
}
