package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class Version {
    private int major;
    private int minor;

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Version)) {
            return false;
        } else {
            Version other = (Version)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getMajor() != other.getMajor()) {
                return false;
            } else {
                return this.getMinor() == other.getMinor();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Version;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getMajor();
        result = result * 59 + this.getMinor();
        return result;
    }

    public String toString() {
        return "Version(major=" + this.getMajor() + ", minor=" + this.getMinor() + ")";
    }

    public Version() {
    }

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }
}
