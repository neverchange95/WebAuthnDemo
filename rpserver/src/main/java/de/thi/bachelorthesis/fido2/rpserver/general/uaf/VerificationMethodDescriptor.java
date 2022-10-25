package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class VerificationMethodDescriptor {
    private int userVerification;
    private CodeAccuracyDescriptor caDesc;
    private BiometricAccuracyDescriptor baDesc;
    private PatternAccuracyDescriptor paDesc;

    public int getUserVerification() {
        return this.userVerification;
    }

    public CodeAccuracyDescriptor getCaDesc() {
        return this.caDesc;
    }

    public BiometricAccuracyDescriptor getBaDesc() {
        return this.baDesc;
    }

    public PatternAccuracyDescriptor getPaDesc() {
        return this.paDesc;
    }

    public void setUserVerification(int userVerification) {
        this.userVerification = userVerification;
    }

    public void setCaDesc(CodeAccuracyDescriptor caDesc) {
        this.caDesc = caDesc;
    }

    public void setBaDesc(BiometricAccuracyDescriptor baDesc) {
        this.baDesc = baDesc;
    }

    public void setPaDesc(PatternAccuracyDescriptor paDesc) {
        this.paDesc = paDesc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof VerificationMethodDescriptor)) {
            return false;
        } else {
            VerificationMethodDescriptor other = (VerificationMethodDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getUserVerification() != other.getUserVerification()) {
                return false;
            } else {
                label49: {
                    Object this$caDesc = this.getCaDesc();
                    Object other$caDesc = other.getCaDesc();
                    if (this$caDesc == null) {
                        if (other$caDesc == null) {
                            break label49;
                        }
                    } else if (this$caDesc.equals(other$caDesc)) {
                        break label49;
                    }

                    return false;
                }

                Object this$baDesc = this.getBaDesc();
                Object other$baDesc = other.getBaDesc();
                if (this$baDesc == null) {
                    if (other$baDesc != null) {
                        return false;
                    }
                } else if (!this$baDesc.equals(other$baDesc)) {
                    return false;
                }

                Object this$paDesc = this.getPaDesc();
                Object other$paDesc = other.getPaDesc();
                if (this$paDesc == null) {
                    if (other$paDesc != null) {
                        return false;
                    }
                } else if (!this$paDesc.equals(other$paDesc)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof VerificationMethodDescriptor;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getUserVerification();
        Object $caDesc = this.getCaDesc();
        result = result * 59 + ($caDesc == null ? 43 : $caDesc.hashCode());
        Object $baDesc = this.getBaDesc();
        result = result * 59 + ($baDesc == null ? 43 : $baDesc.hashCode());
        Object $paDesc = this.getPaDesc();
        result = result * 59 + ($paDesc == null ? 43 : $paDesc.hashCode());
        return result;
    }

    public String toString() {
        return "VerificationMethodDescriptor(userVerification=" + this.getUserVerification() + ", caDesc=" + this.getCaDesc() + ", baDesc=" + this.getBaDesc() + ", paDesc=" + this.getPaDesc() + ")";
    }

    public VerificationMethodDescriptor(int userVerification, CodeAccuracyDescriptor caDesc, BiometricAccuracyDescriptor baDesc, PatternAccuracyDescriptor paDesc) {
        this.userVerification = userVerification;
        this.caDesc = caDesc;
        this.baDesc = baDesc;
        this.paDesc = paDesc;
    }

    public VerificationMethodDescriptor() {
    }
}
