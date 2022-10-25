package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class BiometricAccuracyDescriptor {
    private Double FAR;
    private Double FRR;
    private Double EER;
    private Double FAAR;
    private Integer maxReferenceDataSets;
    private Integer maxRetries;
    private Integer blockSlowdown;

    public Double getFAR() {
        return this.FAR;
    }

    public Double getFRR() {
        return this.FRR;
    }

    public Double getEER() {
        return this.EER;
    }

    public Double getFAAR() {
        return this.FAAR;
    }

    public Integer getMaxReferenceDataSets() {
        return this.maxReferenceDataSets;
    }

    public Integer getMaxRetries() {
        return this.maxRetries;
    }

    public Integer getBlockSlowdown() {
        return this.blockSlowdown;
    }

    public void setFAR(Double FAR) {
        this.FAR = FAR;
    }

    public void setFRR(Double FRR) {
        this.FRR = FRR;
    }

    public void setEER(Double EER) {
        this.EER = EER;
    }

    public void setFAAR(Double FAAR) {
        this.FAAR = FAAR;
    }

    public void setMaxReferenceDataSets(Integer maxReferenceDataSets) {
        this.maxReferenceDataSets = maxReferenceDataSets;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setBlockSlowdown(Integer blockSlowdown) {
        this.blockSlowdown = blockSlowdown;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BiometricAccuracyDescriptor)) {
            return false;
        } else {
            BiometricAccuracyDescriptor other = (BiometricAccuracyDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label95: {
                    Object this$FAR = this.getFAR();
                    Object other$FAR = other.getFAR();
                    if (this$FAR == null) {
                        if (other$FAR == null) {
                            break label95;
                        }
                    } else if (this$FAR.equals(other$FAR)) {
                        break label95;
                    }

                    return false;
                }

                Object this$FRR = this.getFRR();
                Object other$FRR = other.getFRR();
                if (this$FRR == null) {
                    if (other$FRR != null) {
                        return false;
                    }
                } else if (!this$FRR.equals(other$FRR)) {
                    return false;
                }

                Object this$EER = this.getEER();
                Object other$EER = other.getEER();
                if (this$EER == null) {
                    if (other$EER != null) {
                        return false;
                    }
                } else if (!this$EER.equals(other$EER)) {
                    return false;
                }

                label74: {
                    Object this$FAAR = this.getFAAR();
                    Object other$FAAR = other.getFAAR();
                    if (this$FAAR == null) {
                        if (other$FAAR == null) {
                            break label74;
                        }
                    } else if (this$FAAR.equals(other$FAAR)) {
                        break label74;
                    }

                    return false;
                }

                label67: {
                    Object this$maxReferenceDataSets = this.getMaxReferenceDataSets();
                    Object other$maxReferenceDataSets = other.getMaxReferenceDataSets();
                    if (this$maxReferenceDataSets == null) {
                        if (other$maxReferenceDataSets == null) {
                            break label67;
                        }
                    } else if (this$maxReferenceDataSets.equals(other$maxReferenceDataSets)) {
                        break label67;
                    }

                    return false;
                }

                Object this$maxRetries = this.getMaxRetries();
                Object other$maxRetries = other.getMaxRetries();
                if (this$maxRetries == null) {
                    if (other$maxRetries != null) {
                        return false;
                    }
                } else if (!this$maxRetries.equals(other$maxRetries)) {
                    return false;
                }

                Object this$blockSlowdown = this.getBlockSlowdown();
                Object other$blockSlowdown = other.getBlockSlowdown();
                if (this$blockSlowdown == null) {
                    if (other$blockSlowdown != null) {
                        return false;
                    }
                } else if (!this$blockSlowdown.equals(other$blockSlowdown)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BiometricAccuracyDescriptor;
    }

    public int hashCode() {
        int result = 1;
        Object $FAR = this.getFAR();
        result = result * 59 + ($FAR == null ? 43 : $FAR.hashCode());
        Object $FRR = this.getFRR();
        result = result * 59 + ($FRR == null ? 43 : $FRR.hashCode());
        Object $EER = this.getEER();
        result = result * 59 + ($EER == null ? 43 : $EER.hashCode());
        Object $FAAR = this.getFAAR();
        result = result * 59 + ($FAAR == null ? 43 : $FAAR.hashCode());
        Object $maxReferenceDataSets = this.getMaxReferenceDataSets();
        result = result * 59 + ($maxReferenceDataSets == null ? 43 : $maxReferenceDataSets.hashCode());
        Object $maxRetries = this.getMaxRetries();
        result = result * 59 + ($maxRetries == null ? 43 : $maxRetries.hashCode());
        Object $blockSlowdown = this.getBlockSlowdown();
        result = result * 59 + ($blockSlowdown == null ? 43 : $blockSlowdown.hashCode());
        return result;
    }

    public String toString() {
        return "BiometricAccuracyDescriptor(FAR=" + this.getFAR() + ", FRR=" + this.getFRR() + ", EER=" + this.getEER() + ", FAAR=" + this.getFAAR() + ", maxReferenceDataSets=" + this.getMaxReferenceDataSets() + ", maxRetries=" + this.getMaxRetries() + ", blockSlowdown=" + this.getBlockSlowdown() + ")";
    }

    public BiometricAccuracyDescriptor(Double FAR, Double FRR, Double EER, Double FAAR, Integer maxReferenceDataSets, Integer maxRetries, Integer blockSlowdown) {
        this.FAR = FAR;
        this.FRR = FRR;
        this.EER = EER;
        this.FAAR = FAAR;
        this.maxReferenceDataSets = maxReferenceDataSets;
        this.maxRetries = maxRetries;
        this.blockSlowdown = blockSlowdown;
    }

    public BiometricAccuracyDescriptor() {
    }
}
