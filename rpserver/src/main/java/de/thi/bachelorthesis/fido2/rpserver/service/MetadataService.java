package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.general.uaf.MetadataStatement;

import java.util.List;

public interface MetadataService {
    MetadataStatement getMetadataStatementWithAaguid(String aaguid);
    List<MetadataStatement> getAllU2FMetadataStatements();
    List<MetadataStatement> getAllMetadataStatements();
}
