package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.entity.AuthenticatorTransportEntity;
import de.thi.bachelorthesis.fido2.rpserver.entity.RpEntity;
import de.thi.bachelorthesis.fido2.rpserver.entity.UserKeyEntity;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorTransport;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import de.thi.bachelorthesis.fido2.rpserver.general.extension.CredentialProtectionPolicy;
import de.thi.bachelorthesis.fido2.rpserver.model.UserKey;
import de.thi.bachelorthesis.fido2.rpserver.repository.RpRepository;
import de.thi.bachelorthesis.fido2.rpserver.repository.UserKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
public class UserKeyServiceImpl implements UserKeyService{
    private final UserKeyRepository userKeyRepository;
    private final RpRepository rpRepository;

    @Autowired
    public UserKeyServiceImpl(UserKeyRepository userKeyRepository, RpRepository rpRepository) {
        this.userKeyRepository = userKeyRepository;
        this.rpRepository = rpRepository;
    }

    @Override
    public UserKey createUser(UserKey user) {
        UserKeyEntity userKeyEntity = convert(user);

        if (user.getTransports() != null && !user.getTransports().isEmpty()) {
            for (AuthenticatorTransport authenticatorTransport : user.getTransports()) {
                AuthenticatorTransportEntity authenticatorTransportEntity = new AuthenticatorTransportEntity(authenticatorTransport.getValue());
                authenticatorTransportEntity.setUserKeyEntity(userKeyEntity);
                userKeyEntity.getTransports().add(authenticatorTransportEntity);
            }
        }

        userKeyEntity.setRegisteredTimestamp(new Date());
        userKeyRepository.save(userKeyEntity);
        return user;
    }

    @Override
    public boolean isRegistered(String rpId, String userId) {
        List<UserKeyEntity> userKeyEntities = userKeyRepository.findAllByRpEntityIdAndUserId(rpId, userId);
        return userKeyEntities != null && !userKeyEntities.isEmpty();
    }

    @Override
    public boolean containsCredential(String rpId, String credentialId) {
        UserKeyEntity userKeyEntity = userKeyRepository.findByRpEntityIdAndCredentialId(rpId, credentialId);
        return userKeyEntity != null;
    }

    @Override
    public List<UserKey> getWithUserId(String rpId, String userId) {
        List<UserKey> userKeys = new ArrayList<>();
        userKeyRepository.findAllByRpEntityIdAndUserId(rpId, userId)
                .forEach(userKeyEntity -> userKeys.add(convert(userKeyEntity)));
        return userKeys;
    }

    @Override
    public UserKey getWithCredentialId(String rpId, String credentialId) {
        UserKeyEntity userKeyEntity = userKeyRepository
                .findByRpEntityIdAndCredentialId(rpId, credentialId);
        return convert(userKeyEntity);
    }

    @Override
    public List<UserKey> getWithUserIdAndAaguid(String rpId, String userId, String aaguid) {
        return null;
    }

    @Override
    public void update(UserKey user) {
        UserKeyEntity userKeyEntity = userKeyRepository
                .findByRpEntityIdAndCredentialId(user.getRpId(), user.getCredentialId());
        userKeyEntity.setSignCounter(user.getSignCounter());
        userKeyEntity.setAuthenticatedTimestamp(new Date());
        userKeyRepository.save(userKeyEntity);
    }

    @Override
    public void deleteWithUserId(String rpId, String userId) {
        List<UserKeyEntity> userKeyEntities = userKeyRepository.findAllByRpEntityIdAndUserId(rpId, userId);
        if (userKeyEntities == null ||
                userKeyEntities.isEmpty()) {
            throw FIDO2ServerRuntimeException.makeCredNotFoundUser(rpId, userId);
        }
        userKeyEntities.forEach(userKeyRepository::delete);
    }

    @Override
    public void deleteWithCredentialId(String rpId, String credentialId) {
        UserKeyEntity userKeyEntity = userKeyRepository
                .findByRpEntityIdAndCredentialId(rpId, credentialId);
        if (userKeyEntity == null) {
            throw FIDO2ServerRuntimeException.makeCredNotFound(rpId, credentialId);
        }
        userKeyRepository.delete(userKeyEntity);
    }

    private UserKeyEntity convert(UserKey userKey) {
        Optional<RpEntity> optionalRpEntity = rpRepository.findById(userKey.getRpId());
        RpEntity rpEntity;
        if (optionalRpEntity.isPresent()) {
            rpEntity = optionalRpEntity.get();
        } else {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.RPID_NOT_FOUND, "RpId: " + userKey.getRpId());
        }

        UserKeyEntity.UserKeyEntityBuilder builder = UserKeyEntity
                .builder()
                .rpEntity(rpEntity)
                .publicKey(Base64.getUrlEncoder().withoutPadding().encodeToString(userKey.getPublicKey().getEncoded()))
                .userDisplayName(userKey.getDisplayName())
                .userIcon(userKey.getIcon())
                .userId(userKey.getId())
                .aaguid(userKey.getAaguid())
                .attestationType(userKey.getAttestationType())
                .credentialId(userKey.getCredentialId())
                .signatureAlgorithm(userKey.getAlgorithm().getValue())
                .signCounter(userKey.getSignCounter())
                .username(userKey.getName())
                .transports(new ArrayList<>())
                .rk(userKey.getRk())
                .credProtect(userKey.getCredProtect());

        if (userKey.getCredProtect() == null) {
            builder.credProtect(CredentialProtectionPolicy.USER_VERIFICATION_OPTIONAL.getValue());  // default
        }

        return builder.build();
    }

    private UserKey convert(UserKeyEntity userKeyEntity) {
        byte[] encodedPublicKey = Base64.getUrlDecoder().decode(userKeyEntity.getPublicKey());
        COSEAlgorithm algorithm = COSEAlgorithm.fromValue(userKeyEntity.getSignatureAlgorithm());
        PublicKey publicKey;
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory keyFactory;
        try {
            if (algorithm.isRSAAlgorithm()) {
                keyFactory = KeyFactory.getInstance("RSA");
            } else if (algorithm.isEdDSAAlgorithm()) {
                keyFactory = KeyFactory.getInstance("EdDSA");
            } else {
                keyFactory = KeyFactory.getInstance("ECDSA");
            }
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw FIDO2ServerRuntimeException.makeCryptoError(e);

        }

        List<AuthenticatorTransport> authenticatorTransports = null;
        if (userKeyEntity.getTransports() != null && !userKeyEntity.getTransports().isEmpty()) {
            authenticatorTransports = new ArrayList<>();
            for (AuthenticatorTransportEntity authenticatorTransportEntity : userKeyEntity.getTransports()) {
                authenticatorTransports.add(AuthenticatorTransport.fromValue(authenticatorTransportEntity.getTransport()));
            }
        }

        UserKey.UserKeyBuilder builder = UserKey
                .builder()
                .aaguid(userKeyEntity.getAaguid())
                .algorithm(algorithm)
                .attestationType(userKeyEntity.getAttestationType())
                .credentialId(userKeyEntity.getCredentialId())
                .displayName(userKeyEntity.getUserDisplayName())
                .id(userKeyEntity.getUserId())
                .name(userKeyEntity.getUsername())
                .publicKey(publicKey)
                .rpId(userKeyEntity.getRpEntity().getId())
                .signCounter(userKeyEntity.getSignCounter())
                .icon(userKeyEntity.getUserIcon())
                .transports(authenticatorTransports)
                .rk(userKeyEntity.getRk())
                .credProtect(userKeyEntity.getCredProtect())
                .registeredAt(userKeyEntity.getRegisteredTimestamp())
                .authenticatedAt(userKeyEntity.getAuthenticatedTimestamp());

        if (userKeyEntity.getCredProtect() == null) {
            builder.credProtect(CredentialProtectionPolicy.USER_VERIFICATION_OPTIONAL.getValue());  // default
        }

        return builder.build();
    }
}
