package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import lombok.Data;

import java.util.List;

@Data
public class MetadataObject {
    private String identifier;
    private long version;
    private List<String> trustedCertificates;
    private VendorInfo vendorInfo;
    private List<DeviceInfo> devices;
}
