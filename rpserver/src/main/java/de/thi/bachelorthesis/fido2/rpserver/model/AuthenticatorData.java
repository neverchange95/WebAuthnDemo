package de.thi.bachelorthesis.fido2.rpserver.model;

import de.thi.bachelorthesis.fido2.rpserver.util.UnsignedUtil;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Data
@SuperBuilder
public class AuthenticatorData {

    static final int UP_MASK = 1;
    static final int UV_MASK = 1 << 2;
    static final int AT_MASK = 1 << 6;
    static final int ED_MASK = 1 << 7;

    private byte[] rpIdHash;
    private boolean userPresent;
    private boolean userVerified;
    private boolean atIncluded;
    private boolean edIncluded;
    private long signCount;
    private AttestedCredentialData attestedCredentialData;
    private AuthenticatorExtension extensions;
    private byte[] bytes;

    public static AuthenticatorData decode(byte[] encoded) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(encoded);

        // rpIdHash
        byte[] rpIdHash = new byte[32];
        inputStream.read(rpIdHash, 0, rpIdHash.length);

        // flags
        int flags = inputStream.read();
        AuthenticatorData authenticatorData = decodeAuthenticatorDataCommon(encoded, inputStream, rpIdHash, flags);

        AuthenticatorExtension extensions = null;
        boolean edIncluded = (flags & ED_MASK) == ED_MASK;
        if (edIncluded) {
            extensions = decodeAuthenticatorDataExtension(inputStream);
        }
        authenticatorData.setExtensions(extensions);
        authenticatorData.setEdIncluded(edIncluded);

        if (inputStream.available() > 0) {
            // remaining byte array
            throw new IOException("Attestation data contains left over bytes " + inputStream.available());
        }

        return authenticatorData;
    }

    protected static AuthenticatorData decodeAuthenticatorDataCommon(byte[] encoded, ByteArrayInputStream inputStream, byte[] rpIdHash, int flags) throws IOException {

        boolean userPresent = (flags & UP_MASK) == UP_MASK;
        boolean userVerified = (flags & UV_MASK) == UV_MASK;
        boolean atIncluded = (flags & AT_MASK) == AT_MASK;

        AttestedCredentialData attestedCredentialData = null;

        // signCounter
        byte[] signCounterBytes = new byte[4];
        inputStream.read(signCounterBytes, 0, signCounterBytes.length);
        long signCounter = UnsignedUtil.readUINT32BE(signCounterBytes);

        // attested cred. data
        if (atIncluded) {
            // aaguid
            byte[] aaguidBytes = new byte[16];
            inputStream.read(aaguidBytes, 0, aaguidBytes.length);

            // credentialIdLength
            byte[] credentialIdLengthBytes = new byte[2];
            inputStream.read(credentialIdLengthBytes, 0, credentialIdLengthBytes.length);
            int credentialIdLength = UnsignedUtil.readUINT16BE(credentialIdLengthBytes);

            // credentialId
            byte[] credentialIdBytes = new byte[credentialIdLength];
            inputStream.read(credentialIdBytes, 0, credentialIdLength);

            // cbor decoding
            CredentialPublicKey credentialPublicKey = CredentialPublicKey.decode(inputStream);

            attestedCredentialData = new AttestedCredentialData();
            attestedCredentialData.setAaguid(aaguidBytes);
            attestedCredentialData.setCredentialId(credentialIdBytes);
            attestedCredentialData.setCredentialPublicKey(credentialPublicKey);
        }

        return AuthenticatorData.builder()
                .rpIdHash(rpIdHash)
                .signCount(signCounter)
                .userPresent(userPresent)
                .userVerified(userVerified)
                .atIncluded(atIncluded)
                .bytes(encoded)
                .attestedCredentialData(attestedCredentialData)
                .build();
    }

    protected static AuthenticatorExtension decodeAuthenticatorDataExtension(ByteArrayInputStream inputStream) throws IOException {
        int extensionDataLength = inputStream.available();
        if (extensionDataLength > 0) {
            byte[] extensionsBytes = new byte[extensionDataLength];
            inputStream.read(extensionsBytes);
            return AuthenticatorExtension.decode(extensionsBytes);
        } else {
            throw new IOException("No available bytes in Authenticator data for extensions");
        }
    }
}
