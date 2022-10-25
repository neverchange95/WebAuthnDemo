package de.thi.bachelorthesis.fido2.rpserver.util;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;

public class SignatureUtil {
    // RSA PSS
    public static boolean verifySHA256withRSAPssSignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA/PSS");
        MGF1ParameterSpec mgf1spec = MGF1ParameterSpec.SHA256;
        signature.setParameter(new PSSParameterSpec(mgf1spec.getDigestAlgorithm(), "MGF1",
                mgf1spec, 32, 1));

        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA384withRSAPssSignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA384withRSA/PSS");
        MGF1ParameterSpec mgf1spec = MGF1ParameterSpec.SHA384;
        signature.setParameter(new PSSParameterSpec(mgf1spec.getDigestAlgorithm(), "MGF1",
                mgf1spec, 48, 1));

        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA512withRSAPssSignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA512withRSA/PSS");
        MGF1ParameterSpec mgf1spec = MGF1ParameterSpec.SHA512;
        signature.setParameter(new PSSParameterSpec(mgf1spec.getDigestAlgorithm(), "MGF1",
                mgf1spec, 64, 1));

        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }


    public static byte[] signSHA256withRSAPssSignature(PrivateKey privateKey, byte[] messageBytes)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA/PSS");
        MGF1ParameterSpec mgf1spec = MGF1ParameterSpec.SHA256;
        signature.setParameter(new PSSParameterSpec(mgf1spec.getDigestAlgorithm(), "MGF1",
                mgf1spec, 32, 1));
        signature.initSign(privateKey);
        signature.update(messageBytes);

        return signature.sign();
    }

    // RSA PKCS v1.5
    public static boolean verifySHA1withRSASignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA256withRSASignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA384withRSASignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA384withRSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA512withRSASignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA512withRSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA256withECDSA(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA384withECDSA(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA384withECDSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifySHA512withECDSA(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static boolean verifyPureEdDSA(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("NONEwithEdDSA");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    /**
     * Get DER encoded ECDSA signature from raw encoded signature
     * @param rawSignature
     * @return
     * @throws IOException
     */
    public static byte[] getDEREncodedECDSASignature(byte[] rawSignature) throws IOException {
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(rawSignature, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(rawSignature, 32, 64));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(72);

        DERSequenceGenerator seq = new DERSequenceGenerator(byteArrayOutputStream);
        seq.addObject(new ASN1Integer(r));
        seq.addObject(new ASN1Integer(s));
        seq.close();

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Get raw RSASA-PSS signature from DER encoded signature
     * @param derSignature
     * @return
     * @throws IOException
     */
    public static byte[] getRawSignatureFromDEREncodedRSASAPSSSignature(byte[] derSignature) throws IOException {
        byte[] rawSignature = new byte[derSignature.length - 4];
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(derSignature);
        byteArrayInputStream.skip(4);
        byteArrayInputStream.read(rawSignature);

        return rawSignature;
    }
}
