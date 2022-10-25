package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class PatternAccuracyDescriptor {
    private long minComplexity;
    private Integer maxRetries;
    private Integer blockSlowdown;

    public long getMinComplexity() {
        return this.minComplexity;
    }

    public Integer getMaxRetries() {
        return this.maxRetries;
    }

    public Integer getBlockSlowdown() {
        return this.blockSlowdown;
    }

    public void setMinComplexity(long minComplexity) {
        this.minComplexity = minComplexity;
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
        } else if (!(o instanceof PatternAccuracyDescriptor)) {
            return false;
        } else {
            PatternAccuracyDescriptor other = (PatternAccuracyDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getMinComplexity() != other.getMinComplexity()) {
                return false;
            } else {
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
        return other instanceof PatternAccuracyDescriptor;
    }

    public int hashCode() {
        int result = 1;
        long $minComplexity = this.getMinComplexity();
        result = result * 59 + (int)($minComplexity >>> 32 ^ $minComplexity);
        Object $maxRetries = this.getMaxRetries();
        result = result * 59 + ($maxRetries == null ? 43 : $maxRetries.hashCode());
        Object $blockSlowdown = this.getBlockSlowdown();
        result = result * 59 + ($blockSlowdown == null ? 43 : $blockSlowdown.hashCode());
        return result;
    }

    public String toString() {
        return "PatternAccuracyDescriptor(minComplexity=" + this.getMinComplexity() + ", maxRetries=" + this.getMaxRetries() + ", blockSlowdown=" + this.getBlockSlowdown() + ")";
    }

    public PatternAccuracyDescriptor(long minComplexity, Integer maxRetries, Integer blockSlowdown) {
        this.minComplexity = minComplexity;
        this.maxRetries = maxRetries;
        this.blockSlowdown = blockSlowdown;
    }

    public PatternAccuracyDescriptor() {
    }
}
