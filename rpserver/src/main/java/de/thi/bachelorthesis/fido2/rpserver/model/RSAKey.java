package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import de.thi.bachelorthesis.fido2.rpserver.cose.COSEKeyType;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Builder
public class RSAKey extends CredentialPublicKey {
    private COSEAlgorithm algorithm;
    private byte[] n;
    private byte[] e;

    @Override
    public byte[] encode() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CBORFactory factory = new CBORFactory();
        CBORGenerator gen = factory.createGenerator(outputStream);

        // start map
        gen.writeStartObject();

        gen.writeFieldId(1);    // kty label
        gen.writeNumber(COSEKeyType.RSA.getValue()); // EC2 kty

        gen.writeFieldId(3);    // alg label
        gen.writeNumber(algorithm.getValue());  // alg value

        gen.writeFieldId(-1);   // n label
        gen.writeBinary(n); // n value

        gen.writeFieldId(-2);   // e label
        gen.writeBinary(e); // e value

        // end map
        gen.writeEndObject();

        gen.close();

        return outputStream.toByteArray();
    }
}
