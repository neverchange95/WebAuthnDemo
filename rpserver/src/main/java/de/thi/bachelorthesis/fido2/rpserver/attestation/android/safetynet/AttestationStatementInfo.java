package de.thi.bachelorthesis.fido2.rpserver.attestation.android.safetynet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttestationStatementInfo {
    private String nonce;
    private long timestampMs;
    private String apkPackageName;
    private List<String> apkCertificateDigestSha256;
    private String apkDigestSha256;
    private boolean ctsProfileMatch;
    private boolean basicIntegrity;
    private String advice;
}
