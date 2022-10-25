package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerRegPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.general.TokenBinding;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.AuthenticationResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.RegistrationResponse;

public interface ResponseService {
    RegistrationResponse handleAttestation(ServerRegPublicKeyCredential serverPublicKeyCredential, String sessionId,
                                           String origin, String rpId, TokenBinding tokenBinding);

    AuthenticationResponse handleAssertion(ServerAuthPublicKeyCredential serverPublicKeyCredential, String sessionId,
                                           String origin, String rpId, TokenBinding tokenBinding);
}
