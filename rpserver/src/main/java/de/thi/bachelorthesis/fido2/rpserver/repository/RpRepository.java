package de.thi.bachelorthesis.fido2.rpserver.repository;

import de.thi.bachelorthesis.fido2.rpserver.entity.RpEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpRepository extends CrudRepository<RpEntity, String> {

}
