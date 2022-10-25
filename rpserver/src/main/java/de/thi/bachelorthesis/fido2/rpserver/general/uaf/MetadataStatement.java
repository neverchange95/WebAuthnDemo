package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MetadataStatement {
    private String aaid;
    private String aaguid;
    private List<String> attestationCertificateKeyIdentifiers;
    private String description;
    private int authenticatorVersion;
    private String protocolFamily;
    private Version[] upv;
    private String assertionScheme;
    private int authenticationAlgorithm;
    private int publicKeyAlgAndEncoding;
    private List<Integer> attestationTypes;
    private VerificationMethodDescriptor[][] userVerificationDetails;
    private int keyProtection;
    private boolean isKeyRestricted;
    private boolean isFreshUserVerificationRequired;
    private int matcherProtection;
    private long attachmentHint;
    private boolean isSecondFactorOnly;
    private int tcDisplay;
    private String tcDisplayContentType;
    private DisplayPNGCharacteristicsDescriptor[] tcDisplayPNGCharacteristics;
    private List<String> attestationRootCertificates;
    private EcdaaTrustAnchor[] ecdaaTrustAnchors;
    private String icon;
    private ExtensionDescriptor[] supportedExtensions;
    private String legalHeader;
    private Map<String, String> alternativeDescriptions;
    private List<Integer> authenticationAlgorithms;
    private List<Integer> publicKeyAlgAndEncodings;
    private int cryptoStrength;
    private String operatingEnv;

    public MetadataStatement() {
    }

    public String getAaid() {
        return this.aaid;
    }

    public String getAaguid() {
        return this.aaguid;
    }

    public List<String> getAttestationCertificateKeyIdentifiers() {
        return this.attestationCertificateKeyIdentifiers;
    }

    public String getDescription() {
        return this.description;
    }

    public int getAuthenticatorVersion() {
        return this.authenticatorVersion;
    }

    public String getProtocolFamily() {
        return this.protocolFamily;
    }

    public Version[] getUpv() {
        return this.upv;
    }

    public String getAssertionScheme() {
        return this.assertionScheme;
    }

    public int getAuthenticationAlgorithm() {
        return this.authenticationAlgorithm;
    }

    public int getPublicKeyAlgAndEncoding() {
        return this.publicKeyAlgAndEncoding;
    }

    public List<Integer> getAttestationTypes() {
        return this.attestationTypes;
    }

    public VerificationMethodDescriptor[][] getUserVerificationDetails() {
        return this.userVerificationDetails;
    }

    public int getKeyProtection() {
        return this.keyProtection;
    }

    public boolean isKeyRestricted() {
        return this.isKeyRestricted;
    }

    public boolean isFreshUserVerificationRequired() {
        return this.isFreshUserVerificationRequired;
    }

    public int getMatcherProtection() {
        return this.matcherProtection;
    }

    public long getAttachmentHint() {
        return this.attachmentHint;
    }

    public boolean isSecondFactorOnly() {
        return this.isSecondFactorOnly;
    }

    public int getTcDisplay() {
        return this.tcDisplay;
    }

    public String getTcDisplayContentType() {
        return this.tcDisplayContentType;
    }

    public DisplayPNGCharacteristicsDescriptor[] getTcDisplayPNGCharacteristics() {
        return this.tcDisplayPNGCharacteristics;
    }

    public List<String> getAttestationRootCertificates() {
        return this.attestationRootCertificates;
    }

    public EcdaaTrustAnchor[] getEcdaaTrustAnchors() {
        return this.ecdaaTrustAnchors;
    }

    public String getIcon() {
        return this.icon;
    }

    public ExtensionDescriptor[] getSupportedExtensions() {
        return this.supportedExtensions;
    }

    public String getLegalHeader() {
        return this.legalHeader;
    }

    public Map<String, String> getAlternativeDescriptions() {
        return this.alternativeDescriptions;
    }

    public List<Integer> getAuthenticationAlgorithms() {
        return this.authenticationAlgorithms;
    }

    public List<Integer> getPublicKeyAlgAndEncodings() {
        return this.publicKeyAlgAndEncodings;
    }

    public int getCryptoStrength() {
        return this.cryptoStrength;
    }

    public String getOperatingEnv() {
        return this.operatingEnv;
    }

    public void setAaid(String aaid) {
        this.aaid = aaid;
    }

    public void setAaguid(String aaguid) {
        this.aaguid = aaguid;
    }

    public void setAttestationCertificateKeyIdentifiers(List<String> attestationCertificateKeyIdentifiers) {
        this.attestationCertificateKeyIdentifiers = attestationCertificateKeyIdentifiers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthenticatorVersion(int authenticatorVersion) {
        this.authenticatorVersion = authenticatorVersion;
    }

    public void setProtocolFamily(String protocolFamily) {
        this.protocolFamily = protocolFamily;
    }

    public void setUpv(Version[] upv) {
        this.upv = upv;
    }

    public void setAssertionScheme(String assertionScheme) {
        this.assertionScheme = assertionScheme;
    }

    public void setAuthenticationAlgorithm(int authenticationAlgorithm) {
        this.authenticationAlgorithm = authenticationAlgorithm;
    }

    public void setPublicKeyAlgAndEncoding(int publicKeyAlgAndEncoding) {
        this.publicKeyAlgAndEncoding = publicKeyAlgAndEncoding;
    }

    public void setAttestationTypes(List<Integer> attestationTypes) {
        this.attestationTypes = attestationTypes;
    }

    public void setUserVerificationDetails(VerificationMethodDescriptor[][] userVerificationDetails) {
        this.userVerificationDetails = userVerificationDetails;
    }

    public void setKeyProtection(int keyProtection) {
        this.keyProtection = keyProtection;
    }

    public void setKeyRestricted(boolean isKeyRestricted) {
        this.isKeyRestricted = isKeyRestricted;
    }

    public void setFreshUserVerificationRequired(boolean isFreshUserVerificationRequired) {
        this.isFreshUserVerificationRequired = isFreshUserVerificationRequired;
    }

    public void setMatcherProtection(int matcherProtection) {
        this.matcherProtection = matcherProtection;
    }

    public void setAttachmentHint(long attachmentHint) {
        this.attachmentHint = attachmentHint;
    }

    public void setSecondFactorOnly(boolean isSecondFactorOnly) {
        this.isSecondFactorOnly = isSecondFactorOnly;
    }

    public void setTcDisplay(int tcDisplay) {
        this.tcDisplay = tcDisplay;
    }

    public void setTcDisplayContentType(String tcDisplayContentType) {
        this.tcDisplayContentType = tcDisplayContentType;
    }

    public void setTcDisplayPNGCharacteristics(DisplayPNGCharacteristicsDescriptor[] tcDisplayPNGCharacteristics) {
        this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
    }

    public void setAttestationRootCertificates(List<String> attestationRootCertificates) {
        this.attestationRootCertificates = attestationRootCertificates;
    }

    public void setEcdaaTrustAnchors(EcdaaTrustAnchor[] ecdaaTrustAnchors) {
        this.ecdaaTrustAnchors = ecdaaTrustAnchors;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSupportedExtensions(ExtensionDescriptor[] supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    public void setLegalHeader(String legalHeader) {
        this.legalHeader = legalHeader;
    }

    public void setAlternativeDescriptions(Map<String, String> alternativeDescriptions) {
        this.alternativeDescriptions = alternativeDescriptions;
    }

    public void setAuthenticationAlgorithms(List<Integer> authenticationAlgorithms) {
        this.authenticationAlgorithms = authenticationAlgorithms;
    }

    public void setPublicKeyAlgAndEncodings(List<Integer> publicKeyAlgAndEncodings) {
        this.publicKeyAlgAndEncodings = publicKeyAlgAndEncodings;
    }

    public void setCryptoStrength(int cryptoStrength) {
        this.cryptoStrength = cryptoStrength;
    }

    public void setOperatingEnv(String operatingEnv) {
        this.operatingEnv = operatingEnv;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MetadataStatement)) {
            return false;
        } else {
            MetadataStatement other = (MetadataStatement)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label255: {
                    Object this$aaid = this.getAaid();
                    Object other$aaid = other.getAaid();
                    if (this$aaid == null) {
                        if (other$aaid == null) {
                            break label255;
                        }
                    } else if (this$aaid.equals(other$aaid)) {
                        break label255;
                    }

                    return false;
                }

                Object this$aaguid = this.getAaguid();
                Object other$aaguid = other.getAaguid();
                if (this$aaguid == null) {
                    if (other$aaguid != null) {
                        return false;
                    }
                } else if (!this$aaguid.equals(other$aaguid)) {
                    return false;
                }

                Object this$attestationCertificateKeyIdentifiers = this.getAttestationCertificateKeyIdentifiers();
                Object other$attestationCertificateKeyIdentifiers = other.getAttestationCertificateKeyIdentifiers();
                if (this$attestationCertificateKeyIdentifiers == null) {
                    if (other$attestationCertificateKeyIdentifiers != null) {
                        return false;
                    }
                } else if (!this$attestationCertificateKeyIdentifiers.equals(other$attestationCertificateKeyIdentifiers)) {
                    return false;
                }

                label234: {
                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description == null) {
                            break label234;
                        }
                    } else if (this$description.equals(other$description)) {
                        break label234;
                    }

                    return false;
                }

                if (this.getAuthenticatorVersion() != other.getAuthenticatorVersion()) {
                    return false;
                } else {
                    Object this$protocolFamily = this.getProtocolFamily();
                    Object other$protocolFamily = other.getProtocolFamily();
                    if (this$protocolFamily == null) {
                        if (other$protocolFamily != null) {
                            return false;
                        }
                    } else if (!this$protocolFamily.equals(other$protocolFamily)) {
                        return false;
                    }

                    if (!Arrays.deepEquals(this.getUpv(), other.getUpv())) {
                        return false;
                    } else {
                        label218: {
                            Object this$assertionScheme = this.getAssertionScheme();
                            Object other$assertionScheme = other.getAssertionScheme();
                            if (this$assertionScheme == null) {
                                if (other$assertionScheme == null) {
                                    break label218;
                                }
                            } else if (this$assertionScheme.equals(other$assertionScheme)) {
                                break label218;
                            }

                            return false;
                        }

                        if (this.getAuthenticationAlgorithm() != other.getAuthenticationAlgorithm()) {
                            return false;
                        } else if (this.getPublicKeyAlgAndEncoding() != other.getPublicKeyAlgAndEncoding()) {
                            return false;
                        } else {
                            Object this$attestationTypes = this.getAttestationTypes();
                            Object other$attestationTypes = other.getAttestationTypes();
                            if (this$attestationTypes == null) {
                                if (other$attestationTypes != null) {
                                    return false;
                                }
                            } else if (!this$attestationTypes.equals(other$attestationTypes)) {
                                return false;
                            }

                            if (!Arrays.deepEquals(this.getUserVerificationDetails(), other.getUserVerificationDetails())) {
                                return false;
                            } else if (this.getKeyProtection() != other.getKeyProtection()) {
                                return false;
                            } else if (this.isKeyRestricted() != other.isKeyRestricted()) {
                                return false;
                            } else if (this.isFreshUserVerificationRequired() != other.isFreshUserVerificationRequired()) {
                                return false;
                            } else if (this.getMatcherProtection() != other.getMatcherProtection()) {
                                return false;
                            } else if (this.getAttachmentHint() != other.getAttachmentHint()) {
                                return false;
                            } else if (this.isSecondFactorOnly() != other.isSecondFactorOnly()) {
                                return false;
                            } else if (this.getTcDisplay() != other.getTcDisplay()) {
                                return false;
                            } else {
                                Object this$tcDisplayContentType = this.getTcDisplayContentType();
                                Object other$tcDisplayContentType = other.getTcDisplayContentType();
                                if (this$tcDisplayContentType == null) {
                                    if (other$tcDisplayContentType != null) {
                                        return false;
                                    }
                                } else if (!this$tcDisplayContentType.equals(other$tcDisplayContentType)) {
                                    return false;
                                }

                                if (!Arrays.deepEquals(this.getTcDisplayPNGCharacteristics(), other.getTcDisplayPNGCharacteristics())) {
                                    return false;
                                } else {
                                    Object this$attestationRootCertificates = this.getAttestationRootCertificates();
                                    Object other$attestationRootCertificates = other.getAttestationRootCertificates();
                                    if (this$attestationRootCertificates == null) {
                                        if (other$attestationRootCertificates != null) {
                                            return false;
                                        }
                                    } else if (!this$attestationRootCertificates.equals(other$attestationRootCertificates)) {
                                        return false;
                                    }

                                    if (!Arrays.deepEquals(this.getEcdaaTrustAnchors(), other.getEcdaaTrustAnchors())) {
                                        return false;
                                    } else {
                                        Object this$icon = this.getIcon();
                                        Object other$icon = other.getIcon();
                                        if (this$icon == null) {
                                            if (other$icon != null) {
                                                return false;
                                            }
                                        } else if (!this$icon.equals(other$icon)) {
                                            return false;
                                        }

                                        if (!Arrays.deepEquals(this.getSupportedExtensions(), other.getSupportedExtensions())) {
                                            return false;
                                        } else {
                                            Object this$legalHeader = this.getLegalHeader();
                                            Object other$legalHeader = other.getLegalHeader();
                                            if (this$legalHeader == null) {
                                                if (other$legalHeader != null) {
                                                    return false;
                                                }
                                            } else if (!this$legalHeader.equals(other$legalHeader)) {
                                                return false;
                                            }

                                            Object this$alternativeDescriptions = this.getAlternativeDescriptions();
                                            Object other$alternativeDescriptions = other.getAlternativeDescriptions();
                                            if (this$alternativeDescriptions == null) {
                                                if (other$alternativeDescriptions != null) {
                                                    return false;
                                                }
                                            } else if (!this$alternativeDescriptions.equals(other$alternativeDescriptions)) {
                                                return false;
                                            }

                                            label151: {
                                                Object this$authenticationAlgorithms = this.getAuthenticationAlgorithms();
                                                Object other$authenticationAlgorithms = other.getAuthenticationAlgorithms();
                                                if (this$authenticationAlgorithms == null) {
                                                    if (other$authenticationAlgorithms == null) {
                                                        break label151;
                                                    }
                                                } else if (this$authenticationAlgorithms.equals(other$authenticationAlgorithms)) {
                                                    break label151;
                                                }

                                                return false;
                                            }

                                            Object this$publicKeyAlgAndEncodings = this.getPublicKeyAlgAndEncodings();
                                            Object other$publicKeyAlgAndEncodings = other.getPublicKeyAlgAndEncodings();
                                            if (this$publicKeyAlgAndEncodings == null) {
                                                if (other$publicKeyAlgAndEncodings != null) {
                                                    return false;
                                                }
                                            } else if (!this$publicKeyAlgAndEncodings.equals(other$publicKeyAlgAndEncodings)) {
                                                return false;
                                            }

                                            if (this.getCryptoStrength() != other.getCryptoStrength()) {
                                                return false;
                                            } else {
                                                Object this$operatingEnv = this.getOperatingEnv();
                                                Object other$operatingEnv = other.getOperatingEnv();
                                                if (this$operatingEnv == null) {
                                                    if (other$operatingEnv != null) {
                                                        return false;
                                                    }
                                                } else if (!this$operatingEnv.equals(other$operatingEnv)) {
                                                    return false;
                                                }

                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof MetadataStatement;
    }

    public int hashCode() {
        int result = 1;
        Object $aaid = this.getAaid();
        result = result * 59 + ($aaid == null ? 43 : $aaid.hashCode());
        Object $aaguid = this.getAaguid();
        result = result * 59 + ($aaguid == null ? 43 : $aaguid.hashCode());
        Object $attestationCertificateKeyIdentifiers = this.getAttestationCertificateKeyIdentifiers();
        result = result * 59 + ($attestationCertificateKeyIdentifiers == null ? 43 : $attestationCertificateKeyIdentifiers.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        result = result * 59 + this.getAuthenticatorVersion();
        Object $protocolFamily = this.getProtocolFamily();
        result = result * 59 + ($protocolFamily == null ? 43 : $protocolFamily.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getUpv());
        Object $assertionScheme = this.getAssertionScheme();
        result = result * 59 + ($assertionScheme == null ? 43 : $assertionScheme.hashCode());
        result = result * 59 + this.getAuthenticationAlgorithm();
        result = result * 59 + this.getPublicKeyAlgAndEncoding();
        Object $attestationTypes = this.getAttestationTypes();
        result = result * 59 + ($attestationTypes == null ? 43 : $attestationTypes.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getUserVerificationDetails());
        result = result * 59 + this.getKeyProtection();
        result = result * 59 + (this.isKeyRestricted() ? 79 : 97);
        result = result * 59 + (this.isFreshUserVerificationRequired() ? 79 : 97);
        result = result * 59 + this.getMatcherProtection();
        long $attachmentHint = this.getAttachmentHint();
        result = result * 59 + (int)($attachmentHint >>> 32 ^ $attachmentHint);
        result = result * 59 + (this.isSecondFactorOnly() ? 79 : 97);
        result = result * 59 + this.getTcDisplay();
        Object $tcDisplayContentType = this.getTcDisplayContentType();
        result = result * 59 + ($tcDisplayContentType == null ? 43 : $tcDisplayContentType.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getTcDisplayPNGCharacteristics());
        Object $attestationRootCertificates = this.getAttestationRootCertificates();
        result = result * 59 + ($attestationRootCertificates == null ? 43 : $attestationRootCertificates.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getEcdaaTrustAnchors());
        Object $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getSupportedExtensions());
        Object $legalHeader = this.getLegalHeader();
        result = result * 59 + ($legalHeader == null ? 43 : $legalHeader.hashCode());
        Object $alternativeDescriptions = this.getAlternativeDescriptions();
        result = result * 59 + ($alternativeDescriptions == null ? 43 : $alternativeDescriptions.hashCode());
        Object $authenticationAlgorithms = this.getAuthenticationAlgorithms();
        result = result * 59 + ($authenticationAlgorithms == null ? 43 : $authenticationAlgorithms.hashCode());
        Object $publicKeyAlgAndEncodings = this.getPublicKeyAlgAndEncodings();
        result = result * 59 + ($publicKeyAlgAndEncodings == null ? 43 : $publicKeyAlgAndEncodings.hashCode());
        result = result * 59 + this.getCryptoStrength();
        Object $operatingEnv = this.getOperatingEnv();
        result = result * 59 + ($operatingEnv == null ? 43 : $operatingEnv.hashCode());
        return result;
    }

    public String toString() {
        return "MetadataStatement(aaid=" + this.getAaid() + ", aaguid=" + this.getAaguid() + ", attestationCertificateKeyIdentifiers=" + this.getAttestationCertificateKeyIdentifiers() + ", description=" + this.getDescription() + ", authenticatorVersion=" + this.getAuthenticatorVersion() + ", protocolFamily=" + this.getProtocolFamily() + ", upv=" + Arrays.deepToString(this.getUpv()) + ", assertionScheme=" + this.getAssertionScheme() + ", authenticationAlgorithm=" + this.getAuthenticationAlgorithm() + ", publicKeyAlgAndEncoding=" + this.getPublicKeyAlgAndEncoding() + ", attestationTypes=" + this.getAttestationTypes() + ", userVerificationDetails=" + Arrays.deepToString(this.getUserVerificationDetails()) + ", keyProtection=" + this.getKeyProtection() + ", isKeyRestricted=" + this.isKeyRestricted() + ", isFreshUserVerificationRequired=" + this.isFreshUserVerificationRequired() + ", matcherProtection=" + this.getMatcherProtection() + ", attachmentHint=" + this.getAttachmentHint() + ", isSecondFactorOnly=" + this.isSecondFactorOnly() + ", tcDisplay=" + this.getTcDisplay() + ", tcDisplayContentType=" + this.getTcDisplayContentType() + ", tcDisplayPNGCharacteristics=" + Arrays.deepToString(this.getTcDisplayPNGCharacteristics()) + ", attestationRootCertificates=" + this.getAttestationRootCertificates() + ", ecdaaTrustAnchors=" + Arrays.deepToString(this.getEcdaaTrustAnchors()) + ", icon=" + this.getIcon() + ", supportedExtensions=" + Arrays.deepToString(this.getSupportedExtensions()) + ", legalHeader=" + this.getLegalHeader() + ", alternativeDescriptions=" + this.getAlternativeDescriptions() + ", authenticationAlgorithms=" + this.getAuthenticationAlgorithms() + ", publicKeyAlgAndEncodings=" + this.getPublicKeyAlgAndEncodings() + ", cryptoStrength=" + this.getCryptoStrength() + ", operatingEnv=" + this.getOperatingEnv() + ")";
    }
}
