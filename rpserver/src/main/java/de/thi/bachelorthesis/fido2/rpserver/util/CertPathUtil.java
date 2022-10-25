package de.thi.bachelorthesis.fido2.rpserver.util;

import java.security.GeneralSecurityException;
import java.security.cert.*;
import java.util.List;
import java.util.Set;

public class CertPathUtil {
    public static boolean validate(List<Certificate> certificateList, Set<TrustAnchor> trustAnchors, boolean revocationEnabled)
            throws GeneralSecurityException {
        CertPath certPath = CertificateUtil.generateCertPath(certificateList);
        return validate(certPath, trustAnchors, revocationEnabled);
    }

    public static boolean validate(CertPath certPath, Set<TrustAnchor> trustAnchors, boolean revocationEnabled)
            throws GeneralSecurityException {
        // set PKIX parameter
        PKIXParameters pkixParameters = new PKIXParameters(trustAnchors);
        pkixParameters.setRevocationEnabled(revocationEnabled);
        // certificate path validation
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
        try {
            certPathValidator.validate(certPath, pkixParameters);
            return true;
        } catch (CertPathValidatorException e) {
            return false;
        }
    }
}
