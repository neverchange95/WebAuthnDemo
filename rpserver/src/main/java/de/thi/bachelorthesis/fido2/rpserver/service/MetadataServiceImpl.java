package de.thi.bachelorthesis.fido2.rpserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.bachelorthesis.fido2.rpserver.entity.MetadataEntity;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.uaf.MetadataStatement;
import de.thi.bachelorthesis.fido2.rpserver.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataServiceImpl implements MetadataService {
    private final ObjectMapper objectMapper;
    private final MetadataRepository metadataRepository;

    @Autowired
    public MetadataServiceImpl(ObjectMapper objectMapper, MetadataRepository metadataRepository) {
        this.objectMapper = objectMapper;
        this.metadataRepository = metadataRepository;
    }

    @Override
    public MetadataStatement getMetadataStatementWithAaguid(String aaguid) {
        MetadataEntity metadataEntity = metadataRepository.findByAaguid(aaguid);
        if (metadataEntity != null) {
            try {
                String metadataContent = metadataEntity.getContent();
                return objectMapper.readValue(metadataContent, MetadataStatement.class);
            } catch (IOException e) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.METADATA_JSON_DESERIALIZE_FAIL, e);
            }
        }
        return null;
    }

    @Override
    public List<MetadataStatement> getAllU2FMetadataStatements() {
        List<MetadataStatement> metadataStatementList = new ArrayList<>();
        metadataRepository.findAllByAaguidIsNull().forEach(m -> {
            try {
                metadataStatementList.add(objectMapper.readValue(m.getContent(), MetadataStatement.class));
            } catch (IOException e) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.METADATA_JSON_DESERIALIZE_FAIL, e);
            }
        });
        return metadataStatementList;
    }

    @Override
    public List<MetadataStatement> getAllMetadataStatements() {
        List<MetadataStatement> metadataStatementList = new ArrayList<>();
        metadataRepository.findAll().forEach(m -> {
            try {
                metadataStatementList.add(objectMapper.readValue(m.getContent(), MetadataStatement.class));
            } catch (IOException e) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.METADATA_JSON_DESERIALIZE_FAIL, e);
            }
        });
        return metadataStatementList;
    }
}
