package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.entity.PublicKeyCredentialRpEntity;

import java.util.List;

public interface RpService {
    boolean contains(String rpId);
    PublicKeyCredentialRpEntity get(String rpId);
    List<PublicKeyCredentialRpEntity> getAll();
}
