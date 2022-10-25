package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.entity.PublicKeyCredentialRpEntity;
import de.thi.bachelorthesis.fido2.rpserver.entity.RpEntity;
import de.thi.bachelorthesis.fido2.rpserver.repository.RpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RpServiceImpl implements RpService {
    private final RpRepository rpRepository;

    @Autowired
    public RpServiceImpl(RpRepository rpRepository) {
        this.rpRepository = rpRepository;
    }

    @Override
    public boolean contains(String rpId) {
        return rpRepository.findById(rpId).isPresent();
    }

    @Override
    public PublicKeyCredentialRpEntity get(String rpId) {
        Optional<RpEntity> optionalRp = rpRepository.findById(rpId);
        if (optionalRp.isPresent()) {
            return convert(optionalRp.get());
        }
        return null;
    }

    @Override
    public List<PublicKeyCredentialRpEntity> getAll() {
        return null;
    }

    public PublicKeyCredentialRpEntity convert(RpEntity rpEntity) {
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity();
        rp.setId(rpEntity.getId());
        rp.setName(rpEntity.getName());
        rp.setIcon(rpEntity.getIcon());
        return rp;
    }
}
