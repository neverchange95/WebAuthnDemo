package de.thi.bachelorthesis.fido2.rpserver.helper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation.AndroidKeyAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.attestation.android.safetynet.AndroidSafetyNetAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.attestation.apple.AppleAnonymousAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.attestation.none.NoneAttestationStatementFormat;
import de.thi.bachelorthesis.fido2.rpserver.attestation.packed.PackedAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.attestation.tpm.TpmAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.attestation.u2f.FidoU2fAttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationObject;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorData;

import java.io.IOException;
import java.util.NoSuchElementException;

public class AttestationObjectDeserializer extends StdDeserializer<AttestationObject>  {

    private static final long serialVersionUID = -4818774933895306258L;

    public AttestationObjectDeserializer() {
        super(AttestationObject.class);
    }

    @Override
    public AttestationObject deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode jsonNode = p.readValueAsTree();
        JsonNode authDataNode = jsonNode.get("authData");
        JsonNode fmtNode = jsonNode.get("fmt");
        JsonNode attStmtNode = jsonNode.get("attStmt");

        // check validity
        if (authDataNode == null) {
            throw new IOException("authData is null");
        }

        if (!(authDataNode instanceof BinaryNode)) {
            throw new IOException("authData value is not binary node");
        }

        if (fmtNode == null) {
            throw new IOException("fmt is null");
        }

        if (!(fmtNode instanceof TextNode)) {
            throw new IOException("fmt value is not text node");
        }

        if (attStmtNode == null) {
            throw new IOException("attStmt is null");
        }

        // decode authData
        AuthenticatorData authenticatorData = AuthenticatorData.decode(authDataNode.binaryValue());

        AttestationStatementFormatIdentifier fmt;
        try {
            fmt = AttestationStatementFormatIdentifier.fromValue(fmtNode.asText());
        } catch (NoSuchElementException e) {
            throw new IOException("not supported fmt identifier " + fmtNode.asText(), e);
        }

        // decode attStmt
        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper objectMapper = new ObjectMapper(cborFactory);
        Class attestationStatement = AttestationStatement.class;

        switch (fmt) {
            case PACKED: {
                attestationStatement = PackedAttestationStatement.class;
                break;
            }
            case TPM: {
                attestationStatement = TpmAttestationStatement.class;
                break;
            }
            case ANDROID_KEY: {
                attestationStatement = AndroidKeyAttestationStatement.class;
                break;
            }
            case ANDROID_SAFETYNET: {
                attestationStatement = AndroidSafetyNetAttestationStatement.class;
                break;
            }
            case FIDO_U2F: {
                attestationStatement = FidoU2fAttestationStatement.class;
                break;
            }
            case APPLE_ANONYMOUS: {
                attestationStatement = AppleAnonymousAttestationStatement.class;
                break;
            }
            case NONE: {
                attestationStatement = NoneAttestationStatementFormat.class;
                break;
            }
            default: {
                // exception
            }

        }
        AttestationStatement attStmt  = (AttestationStatement) objectMapper.treeToValue(attStmtNode, attestationStatement);

        AttestationObject attestationObject = new AttestationObject();
        attestationObject.setFmt(fmt);
        attestationObject.setAttStmt(attStmt);
        attestationObject.setAuthData(authenticatorData);

        return attestationObject;
    }
}
