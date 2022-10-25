package de.thi.bachelorthesis.fido2.rpserver.repository;

import de.thi.bachelorthesis.fido2.rpserver.entity.MetadataYubicoEntity;
import org.springframework.data.repository.CrudRepository;

public interface MetadataYubicoRepository extends CrudRepository<MetadataYubicoEntity, Integer> {
    MetadataYubicoEntity findFirstByOrderByIdDesc();
}
