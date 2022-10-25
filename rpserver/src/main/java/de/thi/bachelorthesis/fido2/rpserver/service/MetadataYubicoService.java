package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.MetadataObject;

@FunctionalInterface
public interface MetadataYubicoService {
    MetadataObject getLatestMetadata();
}
