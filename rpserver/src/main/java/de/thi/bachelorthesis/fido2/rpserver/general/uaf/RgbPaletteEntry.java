package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class RgbPaletteEntry {
    private int r;
    private int g;
    private int b;

    public RgbPaletteEntry() {
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RgbPaletteEntry)) {
            return false;
        } else {
            RgbPaletteEntry other = (RgbPaletteEntry)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getR() != other.getR()) {
                return false;
            } else if (this.getG() != other.getG()) {
                return false;
            } else {
                return this.getB() == other.getB();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RgbPaletteEntry;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getR();
        result = result * 59 + this.getG();
        result = result * 59 + this.getB();
        return result;
    }

    public String toString() {
        return "RgbPaletteEntry(r=" + this.getR() + ", g=" + this.getG() + ", b=" + this.getB() + ")";
    }
}
