package de.thi.bachelorthesis.fido2.rpserver.attestation;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class AttestationVerifierFactory {
    private final Map<AttestationStatementFormatIdentifier, AttestationVerifier> verifierMap;

    @Autowired
    public AttestationVerifierFactory(List<AttestationVerifier> verifierList) {
        verifierMap = verifierList.stream()
                .collect(Collectors.toMap(AttestationVerifier::getIdentifier, Function.identity()));
    }

    public AttestationVerifier getVerifier(AttestationStatementFormatIdentifier identifier) {
        return verifierMap.get(identifier);
    }
}
