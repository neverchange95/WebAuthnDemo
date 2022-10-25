package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorVendor;

import java.util.List;

public interface VendorSpecificMetadataService {
    List<String> getAttestationRootCertificates(AuthenticatorVendor vendor);
}
