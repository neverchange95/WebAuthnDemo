package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

import java.util.Arrays;

public class DisplayPNGCharacteristicsDescriptor {
    private long width;
    private long height;
    private int bitDepth;
    private int colorType;
    private int compression;
    private int filter;
    private int interlace;
    private RgbPaletteEntry[] plte;

    public DisplayPNGCharacteristicsDescriptor() {
    }

    public long getWidth() {
        return this.width;
    }

    public long getHeight() {
        return this.height;
    }

    public int getBitDepth() {
        return this.bitDepth;
    }

    public int getColorType() {
        return this.colorType;
    }

    public int getCompression() {
        return this.compression;
    }

    public int getFilter() {
        return this.filter;
    }

    public int getInterlace() {
        return this.interlace;
    }

    public RgbPaletteEntry[] getPlte() {
        return this.plte;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public void setInterlace(int interlace) {
        this.interlace = interlace;
    }

    public void setPlte(RgbPaletteEntry[] plte) {
        this.plte = plte;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DisplayPNGCharacteristicsDescriptor)) {
            return false;
        } else {
            DisplayPNGCharacteristicsDescriptor other = (DisplayPNGCharacteristicsDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getWidth() != other.getWidth()) {
                return false;
            } else if (this.getHeight() != other.getHeight()) {
                return false;
            } else if (this.getBitDepth() != other.getBitDepth()) {
                return false;
            } else if (this.getColorType() != other.getColorType()) {
                return false;
            } else if (this.getCompression() != other.getCompression()) {
                return false;
            } else if (this.getFilter() != other.getFilter()) {
                return false;
            } else if (this.getInterlace() != other.getInterlace()) {
                return false;
            } else {
                return Arrays.deepEquals(this.getPlte(), other.getPlte());
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DisplayPNGCharacteristicsDescriptor;
    }

    public int hashCode() {
        int result = 1;
        long $width = this.getWidth();
        result = result * 59 + (int)($width >>> 32 ^ $width);
        long $height = this.getHeight();
        result = result * 59 + (int)($height >>> 32 ^ $height);
        result = result * 59 + this.getBitDepth();
        result = result * 59 + this.getColorType();
        result = result * 59 + this.getCompression();
        result = result * 59 + this.getFilter();
        result = result * 59 + this.getInterlace();
        result = result * 59 + Arrays.deepHashCode(this.getPlte());
        return result;
    }

    public String toString() {
        return "DisplayPNGCharacteristicsDescriptor(width=" + this.getWidth() + ", height=" + this.getHeight() + ", bitDepth=" + this.getBitDepth() + ", colorType=" + this.getColorType() + ", compression=" + this.getCompression() + ", filter=" + this.getFilter() + ", interlace=" + this.getInterlace() + ", plte=" + Arrays.deepToString(this.getPlte()) + ")";
    }
}
