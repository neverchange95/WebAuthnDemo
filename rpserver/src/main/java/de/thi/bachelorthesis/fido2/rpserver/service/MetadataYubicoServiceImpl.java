package de.thi.bachelorthesis.fido2.rpserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.bachelorthesis.fido2.rpserver.entity.MetadataYubicoEntity;
import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.MetadataObject;
import de.thi.bachelorthesis.fido2.rpserver.repository.MetadataYubicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MetadataYubicoServiceImpl implements MetadataYubicoService {
    private final ObjectMapper objectMapper;
    private final MetadataYubicoRepository metadataYubicoRepository;

    @Autowired
    public MetadataYubicoServiceImpl(
            ObjectMapper objectMapper,
            MetadataYubicoRepository metadataYubicoRepository) {
        this.objectMapper = objectMapper;
        this.metadataYubicoRepository = metadataYubicoRepository;
    }

    @Override
    public MetadataObject getLatestMetadata() {
        MetadataYubicoEntity metadataYubicoEntity = metadataYubicoRepository.findFirstByOrderByIdDesc();

        String content = metadataYubicoEntity.getContent();
        MetadataObject metadataObject = null;
        try {
            metadataObject = objectMapper.readValue(content, MetadataObject.class);
        } catch (IOException e) {
            System.out.println("Error parsing metadata: " + e.getMessage());
        }
        return metadataObject;
    }
}
