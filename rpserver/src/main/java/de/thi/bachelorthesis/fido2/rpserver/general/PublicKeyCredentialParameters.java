package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

@Data
public class PublicKeyCredentialParameters {
    private PublicKeyCredentialType type;
    private COSEAlgorithmIdentifier alg;
}
