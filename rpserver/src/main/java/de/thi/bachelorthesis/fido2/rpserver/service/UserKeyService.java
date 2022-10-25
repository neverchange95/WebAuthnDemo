package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.model.UserKey;

import java.util.List;

public interface UserKeyService {
    UserKey createUser(UserKey user);
    boolean isRegistered(String rpId, String userId);
    boolean containsCredential(String rpId, String credentialId);
    List<UserKey> getWithUserId(String rpId, String userId);
    UserKey getWithCredentialId(String rpId, String credentialId);
    List<UserKey> getWithUserIdAndAaguid(String rpId, String userId, String aaguid);
    void update(UserKey user);
    void deleteWithUserId(String rpId, String userId);
    void deleteWithCredentialId(String rpId, String credentialId);
}
