package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import de.thi.bachelorthesis.fido2.rpserver.cose.COSEEllipticCurve;
import de.thi.bachelorthesis.fido2.rpserver.cose.COSEKeyType;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Builder
public class ECCKey extends CredentialPublicKey {
    private COSEAlgorithm algorithm;
    private COSEEllipticCurve curve;
    private byte[] x;
    private byte[] y;

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CBORFactory factory = new CBORFactory();
        CBORGenerator gen = factory.createGenerator(outputStream);

        // start map
        gen.writeStartObject();

        gen.writeFieldId(1);    // kty label
        gen.writeNumber(COSEKeyType.EC2.getValue()); // EC2 kty

        gen.writeFieldId(3);    // alg label
        gen.writeNumber(algorithm.getValue());  // alg value

        gen.writeFieldId(-1);   // crv label
        gen.writeNumber(curve.getValue());  // crv value

        gen.writeFieldId(-2);   // x label
        gen.writeBinary(x); // x value

        gen.writeFieldId(-3);   // y label
        gen.writeBinary(y); // y value

        // end map
        gen.writeEndObject();

        gen.close();

        return outputStream.toByteArray();
    }
}
