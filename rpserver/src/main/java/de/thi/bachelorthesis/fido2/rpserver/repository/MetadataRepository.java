package de.thi.bachelorthesis.fido2.rpserver.repository;

import de.thi.bachelorthesis.fido2.rpserver.entity.MetadataEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MetadataRepository extends CrudRepository<MetadataEntity, Long> {
    MetadataEntity findByAaguid(String aaguid);
    List<MetadataEntity> findAllByAaguidIsNull();
}
