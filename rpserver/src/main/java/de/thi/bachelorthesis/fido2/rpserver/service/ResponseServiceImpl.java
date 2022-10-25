package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.*;
import de.thi.bachelorthesis.fido2.rpserver.general.crypto.Digests;
import de.thi.bachelorthesis.fido2.rpserver.helper.CredentialPublicKeyHelper;
import de.thi.bachelorthesis.fido2.rpserver.helper.SignatureHelper;
import de.thi.bachelorthesis.fido2.rpserver.model.*;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.AuthenticationResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.RegistrationResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialCreationOptionsResponse;
import de.thi.bachelorthesis.fido2.rpserver.util.AaguidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Primary
@Service
public class ResponseServiceImpl extends ResponseCommonService implements ResponseService {
    private final SessionService sessionService;
    private final UserKeyService userKeyService;
    private final AttestationService attestationService;


    @Autowired
    public ResponseServiceImpl(SessionService sessionService, UserKeyService userKeyService, AttestationService attestationService) {
        this.sessionService = sessionService;
        this.userKeyService = userKeyService;
        this.attestationService = attestationService;
    }

    @Override
    public RegistrationResponse handleAttestation(ServerRegPublicKeyCredential serverPublicKeyCredential, String sessionId, String origin, String rpId, TokenBinding tokenBinding) {
        Session session = checkSession(sessionId);
        ServerAuthenticatorAttestationResponse attestationResponse = serverPublicKeyCredential.getResponse();
        byte[] clientDataHsh = handleCommon("webauthn.create", session.getRegOptionResponse().getChallenge(), attestationResponse.getClientDataJSON(), origin, tokenBinding);
        AttestationObject attestationObject = attestationService.getAttestationObject(attestationResponse);
        attestationService.attestationObjectValidationCheck(rpId, session.getRegOptionResponse().getAuthenticatorSelection(), attestationObject);
        AttestationVerificationResult attestationVerificationResult = attestationService.verifyAttestation(clientDataHsh, attestationObject);

        // prepare trust anchors, attestation fmt (from metadata service or trusted source)
        if (!attestationVerificationResult.isSuccess()) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.ATTESTATION_SIGNATURE_VERIFICATION_FAIL);
        }

        if (attestationVerificationResult.getType() != AttestationType.SELF && attestationVerificationResult.getType() != AttestationType.NONE) {
            attestationService.verifyAttestationCertificate(attestationObject, attestationVerificationResult);
        }

        RegisterCredentialResult result = getRegisterCredentialResult(session.getRegOptionResponse(), attestationResponse.getTransports(), attestationObject.getAuthData(), attestationVerificationResult, rpId);

        UserKey userKey = userKeyService.getWithCredentialId(rpId, result.getCredentialId());
        // get all registered credentials
        List<UserKey> userKeys = userKeyService.getWithUserId(rpId, userKey.getId());
        List<ServerPublicKeyCredentialDescriptor> allowCredentials = getExcludeAndIncludeCredentials(userKeys);
        if(!StringUtils.isEmpty(userKey.getId())) {
            // if there is no credentials for dedicated to the userId, throw an error
            if(allowCredentials.isEmpty()) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_FOUND, "User Id: " + userKey.getId());
            }
        }

        RegistrationResponse response = new RegistrationResponse();

        // check if "result" == ok, then return true
        if(result.isUserVerified()) {
            response.setAllowCredentials(allowCredentials);
            response.setStatus(Status.OK);
        } else {
            response.setStatus(Status.FAILED);
            response.setErrorMessage("User is not verified!");
        }

        return response;
    }

    @Override
    public AuthenticationResponse handleAssertion(ServerAuthPublicKeyCredential serverPublicKeyCredential, String sessionId, String origin, String rpId, TokenBinding tokenBinding) {
        Session session = checkSession(sessionId);
        ServerAuthenticatorAssertionResponse assertionResponse = serverPublicKeyCredential.getResponse();
        byte[] clientDataHsh = handleCommon("webauthn.get", session.getAuthOptionResponse().getChallenge(), assertionResponse.getClientDataJSON(), origin, tokenBinding);
        byte[] authDataBytes = Base64.getUrlDecoder().decode(serverPublicKeyCredential.getResponse().getAuthenticatorData());
        AuthenticatorData authenticatorData = getAuthData(authDataBytes);
        checkCredentialId(serverPublicKeyCredential, session);

        UserKey userKey = getUserKey(serverPublicKeyCredential, rpId);
        verifyUserHandle(serverPublicKeyCredential, userKey);
        verifyAuthDataValues(rpId, session, authenticatorData, userKey.getAaguid());
        verifySignature(serverPublicKeyCredential, authDataBytes, userKey);
        checkSignCounter(authenticatorData, userKey);

        VerifyCredentialResult result = createVerifyCredentialResult(authenticatorData, userKey);

        // get all registered credentials
        List<UserKey> userKeys = userKeyService.getWithUserId(rpId, userKey.getId());
        List<ServerPublicKeyCredentialDescriptor> allowCredentials = getExcludeAndIncludeCredentials(userKeys);
        if(!StringUtils.isEmpty(userKey.getId())) {
            // if there is no credentials for dedicated to the userId, throw an error
            if(allowCredentials.isEmpty()) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_FOUND, "User Id: " + userKey.getId());
            }
        }

        AuthenticationResponse response = new AuthenticationResponse();

        if(result.isUserPresent() && result.isUserVerified()) {
            response.setUsername(userKey.getName());
            response.setDisplayName(userKey.getDisplayName());
            response.setAllowCredentials(allowCredentials);
            response.setStatus(Status.OK);
        } else {
            response.setStatus(Status.FAILED);
            response.setErrorMessage("User is not present or not verified!");
        }

        return response;
    }

    @Override
    protected void checkOrigin(URI originFromClientData, URI originFromRp) {

    }

    protected Session checkSession(String sessionId) {
        Session session = sessionService.getSession(sessionId);

        if (session == null) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.SESSION_NOT_FOUND,
                    "No such session for session id: (" + sessionId + "), Session may be expired already");
        }
        if (session.isServed()) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.SESSION_ALREADY_REVOKED,
                    "Session is revoked for session id: (" + sessionId + "), Response for the session is handled already");
        }
        return session;
    }

    protected RegisterCredentialResult getRegisterCredentialResult(ServerPublicKeyCredentialCreationOptionsResponse regOptionResponse, List<AuthenticatorTransport> transports, AuthenticatorData authData, AttestationVerificationResult attestationVerificationResult, String rpId) {
        // get credential info
        AttestedCredentialData attestedCredentialData = authData.getAttestedCredentialData();
        String credentialId = Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(attestedCredentialData.getCredentialId());

        // check credential id is duplicated by users
        // if the duplications are exist, we may reject or deleting old registration and registering new one
        if (userKeyService.containsCredential(rpId, credentialId)) {
            // just reject
            throw new FIDO2ServerRuntimeException(InternalErrorCode.DUPLICATED_CREDENTIAL_ID,
                    "Duplicated credential id (" + credentialId + ")");
        }

        // store registration for latter authentication
        UserKey userKey = UserKey
                .builder()
                .publicKey(CredentialPublicKeyHelper.convert(attestedCredentialData.getCredentialPublicKey()))
                .aaguid(AaguidUtil.convert(attestedCredentialData.getAaguid()))
                .credentialId(credentialId)
                .id(regOptionResponse.getUser().getId())
                .name(regOptionResponse.getUser().getName())
                .displayName(regOptionResponse.getUser().getDisplayName())
                .rpId(regOptionResponse.getRp().getId())
                .algorithm(CredentialPublicKeyHelper.getCOSEAlgorithm(attestedCredentialData.getCredentialPublicKey()))
                .signCounter(authData.getSignCount())
                .attestationType(attestationVerificationResult.getType())
                .transports(transports)
                .build();

        userKeyService.createUser(userKey);

        // return registration processing result
        return createRegisterCredentialResult(authData, attestedCredentialData, credentialId, userKey);
    }

    protected RegisterCredentialResult createRegisterCredentialResult(AuthenticatorData authData, AttestedCredentialData attestedCredentialData, String credentialId, UserKey userKey) {
        return RegisterCredentialResult
                .builder()
                .aaguid(AaguidUtil.convert(attestedCredentialData.getAaguid()))
                .credentialId(credentialId)
                .attestationType(userKey.getAttestationType())
                .authenticatorTransports(userKey.getTransports())
                .userVerified(authData.isUserVerified())
                .rk(userKey.getRk())
                .credProtect(userKey.getCredProtect())
                .build();
    }

    protected AuthenticatorData getAuthData(byte[] authDataBytes) {
        AuthenticatorData authData;
        try {
            authData = AuthenticatorData.decode(authDataBytes);
        } catch (IOException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.INVALID_AUTHENTICATOR_DATA);
        }
        return authData;
    }

    protected void checkCredentialId(ServerAuthPublicKeyCredential serverPublicKeyCredential, Session session) {
        // check credential.id is in the allow credential list (if we set the allow credential list)
        boolean credentialIdFound = false;
        if (!session.getAuthOptionResponse().getAllowCredentials().isEmpty()) {
            for (ServerPublicKeyCredentialDescriptor publicKeyCredentialDescriptor : session.getAuthOptionResponse().getAllowCredentials()) {
                if (publicKeyCredentialDescriptor.getId().equals(serverPublicKeyCredential.getId())) {
                    credentialIdFound = true;
                    break;
                }
            }
            if (!credentialIdFound) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_ID_NOT_FOUND);
            }
        }
    }

    protected UserKey getUserKey(ServerAuthPublicKeyCredential serverPublicKeyCredential, String rpId) {
        // get user key
        UserKey userKey = userKeyService.getWithCredentialId(rpId, serverPublicKeyCredential.getId());
        if (userKey == null) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_COUNT_INVALID);
        }
        return userKey;
    }

    protected void verifyUserHandle(ServerAuthPublicKeyCredential serverPublicKeyCredential, UserKey userKey) {
        // check userHandle if it present
        if (!StringUtils.isEmpty(serverPublicKeyCredential.getResponse().getUserHandle())) {
            if (!userKey.getId().equals(serverPublicKeyCredential.getResponse().getUserHandle())) {
                // MUST identical to uerHandle
                throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_HANDLE_NOT_MATCHED, "User handle is not matched", userKey.getAaguid());
            }
        }
    }

    protected void verifyAuthDataValues(String rpId, Session session, AuthenticatorData authData, String aaguid) {
        // verify RP ID (compare with SHA256 hash or RP ID)

        byte[] rpIdHash = Digests.sha256(rpId.getBytes(StandardCharsets.UTF_8));
        if (!Arrays.equals(authData.getRpIdHash(), rpIdHash)) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.RPID_HASH_NOT_MATCHED, "RP ID hash is not matched", aaguid);
        }

        // verify user present flag
        if (!authData.isUserPresent()) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_PRESENCE_FLAG_NOT_SET, "User presence flag not set.", aaguid);
        }

        // verify user verification
        if (session.getAuthOptionResponse().getUserVerification() != null &&
                session.getAuthOptionResponse().getUserVerification() == UserVerificationRequirement.REQUIRED &&
                !authData.isUserVerified()) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_VERIFICATION_FLAG_NOT_SET, "User verification flag not set", aaguid);
        }
    }

    protected void verifySignature(ServerAuthPublicKeyCredential serverPublicKeyCredential, byte[] authDataBytes, UserKey userKey) {
        // prepare toBeSignedMessage
        byte[] cData = Base64.getUrlDecoder().decode(serverPublicKeyCredential.getResponse().getClientDataJSON());
        byte[] hash = Digests.sha256(cData);
        // binary concat of authData and hash
        int toBeSignedMessageSize = authDataBytes.length + hash.length;
        byte[] toBeSignedMessage = ByteBuffer
                .allocate(toBeSignedMessageSize)
                .put(authDataBytes)
                .put(hash)
                .array();

        // verify signature
        byte[] signatureBytes = Base64.getUrlDecoder().decode(serverPublicKeyCredential.getResponse().getSignature());
        boolean result = SignatureHelper.verifySignature(userKey.getPublicKey(), toBeSignedMessage, signatureBytes, userKey.getAlgorithm());
        if (!result) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.ASSERTION_SIGNATURE_VERIFICATION_FAIL, "Signature verification failed", userKey.getAaguid());
        }
    }

    protected void checkSignCounter(AuthenticatorData authData, UserKey userKey) {
        // check signature counter
        if (authData.getSignCount() != 0 || userKey.getSignCounter() != 0) {
            if (authData.getSignCount() > userKey.getSignCounter()) {
                // update
                userKey.setSignCounter(authData.getSignCount());
                userKeyService.update(userKey);
            } else {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.ASSERTION_SIGNATURE_VERIFICATION_FAIL);
                // authenticator is may cloned, reject.
            }
        }
    }

    protected VerifyCredentialResult createVerifyCredentialResult(AuthenticatorData authData, UserKey userKey) {
        return VerifyCredentialResult
                .builder()
                .aaguid(userKey.getAaguid())
                .userId(userKey.getId())
                .userVerified(authData.isUserVerified())
                .userPresent(authData.isUserPresent())
                .build();
    }

    private List<ServerPublicKeyCredentialDescriptor> getExcludeAndIncludeCredentials(List<UserKey> userKeys) {
        List<ServerPublicKeyCredentialDescriptor> publicKeyCredentialDescriptors = new ArrayList<>();
        if (userKeys != null &&
                !userKeys.isEmpty()) {
            for (UserKey userKey : userKeys) {
                ServerPublicKeyCredentialDescriptor serverPublicKeyCredentialDescriptor = new ServerPublicKeyCredentialDescriptor();
                serverPublicKeyCredentialDescriptor.setId(userKey.getCredentialId());
                serverPublicKeyCredentialDescriptor.setType(PublicKeyCredentialType.PUBLIC_KEY);
                serverPublicKeyCredentialDescriptor.setTransports(userKey.getTransports());
                publicKeyCredentialDescriptors.add(serverPublicKeyCredentialDescriptor);
            }
        }

        return publicKeyCredentialDescriptors;
    }
}
