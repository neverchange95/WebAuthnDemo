package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorSelectionCriteria;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthenticatorAttestationResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationObject;

public interface AttestationService {
    AttestationVerificationResult verifyAttestation(byte[] clientDataHsh, AttestationObject attestationObject);
    AttestationObject getAttestationObject(ServerAuthenticatorAttestationResponse attestationResponse);
    void attestationObjectValidationCheck(String rpId, AuthenticatorSelectionCriteria authenticatorSelection, AttestationObject attestationObject);
    void verifyAttestationCertificate(AttestationObject attestationObject, AttestationVerificationResult attestationVerificationResult);
}
