package de.thi.bachelorthesis.fido2.rpserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.thi.bachelorthesis.fido2.rpserver.entity.UserKeyEntity;

import java.util.List;

@Repository
public interface UserKeyRepository extends CrudRepository<UserKeyEntity, Long> {
    List<UserKeyEntity> findAllByRpEntityIdAndUserId(String rpId, String userId);
    UserKeyEntity findByRpEntityIdAndCredentialId(String rpId, String credentialId);
}
