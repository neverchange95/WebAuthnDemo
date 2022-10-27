package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.entity.PublicKeyCredentialRpEntity;
import de.thi.bachelorthesis.fido2.rpserver.entity.ServerPublicKeyCredentialUserEntity;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithmIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.general.PublicKeyCredentialParameters;
import de.thi.bachelorthesis.fido2.rpserver.general.PublicKeyCredentialType;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerPublicKeyCredentialDescriptor;
import de.thi.bachelorthesis.fido2.rpserver.general.crypto.Digests;
import de.thi.bachelorthesis.fido2.rpserver.model.UserKey;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialCreationOptionsRequest;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialCreationOptionsResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialGetOptionsRequest;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialGetOptionsResponse;
import de.thi.bachelorthesis.fido2.rpserver.util.ChallengeGenerator;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Primary
@Service
public class ChallengeServiceImpl implements ChallengeService {
    private final RpService rpService;
    private final UserKeyService userKeyService;


    @Autowired
    public ChallengeServiceImpl(RpService rpService, UserKeyService userKeyService) {
        this.rpService = rpService;
        this.userKeyService = userKeyService;
    }

    @Override
    public ServerPublicKeyCredentialCreationOptionsResponse getRegChallenge(ServerPublicKeyCredentialCreationOptionsRequest regOptionRequest) {
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity();
        rp.setName("Test rp");
        rp.setId("localhost");

        ServerPublicKeyCredentialUserEntity user = new ServerPublicKeyCredentialUserEntity();
        user.setName(regOptionRequest.getUsername());
        user.setId(createUserId(regOptionRequest.getUsername()));
        user.setDisplayName(regOptionRequest.getDisplayName());

        List<UserKey> userKeys = userKeyService.getWithUserId("localhost", user.getId());

        List<PublicKeyCredentialParameters> publicKeyCredentialParameters = new ArrayList<>();
        for (COSEAlgorithmIdentifier identifier : COSEAlgorithmIdentifier.values()) {
            PublicKeyCredentialParameters parameters = new PublicKeyCredentialParameters();
            parameters.setType(PublicKeyCredentialType.PUBLIC_KEY);
            parameters.setAlg(identifier);
            publicKeyCredentialParameters.add(parameters);
        }

        ServerPublicKeyCredentialCreationOptionsResponse serverResponse = ServerPublicKeyCredentialCreationOptionsResponse
                .builder()
                .rp(rp)
                .user(user)
                .excludeCredentials(getExcludeAndIncludeCredentials(userKeys))
                .attestation(regOptionRequest.getAttestation())
                .authenticatorSelection(regOptionRequest.getAuthenticatorSelection())
                .challenge(ChallengeGenerator.generate(64))
                .pubKeyCredParams(publicKeyCredentialParameters)
                .timeout(600000L)
                .build();

        return serverResponse;
    }

    @Override
    public ServerPublicKeyCredentialGetOptionsResponse getAuthChallenge(ServerPublicKeyCredentialGetOptionsRequest authOptionRequest) {
        String rpId = "localhost";
        String userId = createUserId(authOptionRequest.getUsername());

        // get user key
        List<UserKey> userKeys = userKeyService.getWithUserId(rpId, userId);

        // set allowCredentials by searching with rp id and user id
        List<ServerPublicKeyCredentialDescriptor> allowCredentials = getExcludeAndIncludeCredentials(userKeys);
        if(!StringUtils.isEmpty(userId)) {
            // if there is no credentials for dedicated to the userId, throw an error
            if(allowCredentials.isEmpty()) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_FOUND, "User Id: " + userId);
            }
        }

        ServerPublicKeyCredentialGetOptionsResponse serverResponse = ServerPublicKeyCredentialGetOptionsResponse
                .builder()
                .challenge(ChallengeGenerator.generate(64))
                .timeout(600000L)
                .rpId(rpId)
                .allowCredentials(allowCredentials)
                .userVerification(authOptionRequest.getUserVerification())
                .build();

        return serverResponse;
    }

    private String createUserId(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        byte[] digest = Digests.sha256(username.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
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
