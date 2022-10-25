package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

public class AdditionalRevokeChecker {
    public static boolean hasAndroidKeyAttestationRevokedCert(RevokeCheckerClient client, List<Certificate> trustPath) throws IOException {

        Response<ResponseBody> bodyResponse = client.fetchAndroidKeyAttestationRevokeList("attestation/status");

        if (bodyResponse.isSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node;
            if (bodyResponse.body() != null) {
                node = objectMapper.readTree(bodyResponse.body().string()).get("entries");
            } else {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.ANDROID_KEY_ATTESTATION_CERTIFICATE_REVOKED_CHECK_FAILED);
            }
            return trustPath.stream().anyMatch(certificate -> {
                X509Certificate cert = (X509Certificate) certificate;
                String serialNum = cert.getSerialNumber().toString(16).toLowerCase();
                return node.has(serialNum);
            });
        } else {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.ANDROID_KEY_ATTESTATION_CERTIFICATE_REVOKED_CHECK_FAILED);
        }
    }
}
