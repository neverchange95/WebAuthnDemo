package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class CodeAccuracyDescriptor {
    private int base;
    private int minLength;
    private Integer maxRetries;
    private Integer blockSlowdown;

    public int getBase() {
        return this.base;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public Integer getMaxRetries() {
        return this.maxRetries;
    }

    public Integer getBlockSlowdown() {
        return this.blockSlowdown;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
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
        } else if (!(o instanceof CodeAccuracyDescriptor)) {
            return false;
        } else {
            CodeAccuracyDescriptor other = (CodeAccuracyDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getBase() != other.getBase()) {
                return false;
            } else if (this.getMinLength() != other.getMinLength()) {
                return false;
            } else {
                label40: {
                    Object this$maxRetries = this.getMaxRetries();
                    Object other$maxRetries = other.getMaxRetries();
                    if (this$maxRetries == null) {
                        if (other$maxRetries == null) {
                            break label40;
                        }
                    } else if (this$maxRetries.equals(other$maxRetries)) {
                        break label40;
                    }

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
        return other instanceof CodeAccuracyDescriptor;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getBase();
        result = result * 59 + this.getMinLength();
        Object $maxRetries = this.getMaxRetries();
        result = result * 59 + ($maxRetries == null ? 43 : $maxRetries.hashCode());
        Object $blockSlowdown = this.getBlockSlowdown();
        result = result * 59 + ($blockSlowdown == null ? 43 : $blockSlowdown.hashCode());
        return result;
    }

    public String toString() {
        return "CodeAccuracyDescriptor(base=" + this.getBase() + ", minLength=" + this.getMinLength() + ", maxRetries=" + this.getMaxRetries() + ", blockSlowdown=" + this.getBlockSlowdown() + ")";
    }

    public CodeAccuracyDescriptor(int base, int minLength, Integer maxRetries, Integer blockSlowdown) {
        this.base = base;
        this.minLength = minLength;
        this.maxRetries = maxRetries;
        this.blockSlowdown = blockSlowdown;
    }

    public CodeAccuracyDescriptor() {
    }
}
