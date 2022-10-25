package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class EcdaaTrustAnchor {
    private String X;
    private String Y;
    private String c;
    private String sx;
    private String sy;
    private String G1Curve;

    public EcdaaTrustAnchor() {
    }

    public String getX() {
        return this.X;
    }

    public String getY() {
        return this.Y;
    }

    public String getC() {
        return this.c;
    }

    public String getSx() {
        return this.sx;
    }

    public String getSy() {
        return this.sy;
    }

    public String getG1Curve() {
        return this.G1Curve;
    }

    public void setX(String X) {
        this.X = X;
    }

    public void setY(String Y) {
        this.Y = Y;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setSx(String sx) {
        this.sx = sx;
    }

    public void setSy(String sy) {
        this.sy = sy;
    }

    public void setG1Curve(String G1Curve) {
        this.G1Curve = G1Curve;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EcdaaTrustAnchor)) {
            return false;
        } else {
            EcdaaTrustAnchor other = (EcdaaTrustAnchor)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$X = this.getX();
                Object other$X = other.getX();
                if (this$X == null) {
                    if (other$X != null) {
                        return false;
                    }
                } else if (!this$X.equals(other$X)) {
                    return false;
                }

                Object this$Y = this.getY();
                Object other$Y = other.getY();
                if (this$Y == null) {
                    if (other$Y != null) {
                        return false;
                    }
                } else if (!this$Y.equals(other$Y)) {
                    return false;
                }

                Object this$c = this.getC();
                Object other$c = other.getC();
                if (this$c == null) {
                    if (other$c != null) {
                        return false;
                    }
                } else if (!this$c.equals(other$c)) {
                    return false;
                }

                label62: {
                    Object this$sx = this.getSx();
                    Object other$sx = other.getSx();
                    if (this$sx == null) {
                        if (other$sx == null) {
                            break label62;
                        }
                    } else if (this$sx.equals(other$sx)) {
                        break label62;
                    }

                    return false;
                }

                label55: {
                    Object this$sy = this.getSy();
                    Object other$sy = other.getSy();
                    if (this$sy == null) {
                        if (other$sy == null) {
                            break label55;
                        }
                    } else if (this$sy.equals(other$sy)) {
                        break label55;
                    }

                    return false;
                }

                Object this$G1Curve = this.getG1Curve();
                Object other$G1Curve = other.getG1Curve();
                if (this$G1Curve == null) {
                    if (other$G1Curve != null) {
                        return false;
                    }
                } else if (!this$G1Curve.equals(other$G1Curve)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EcdaaTrustAnchor;
    }

    public int hashCode() {
        int result = 1;
        Object $X = this.getX();
        result = result * 59 + ($X == null ? 43 : $X.hashCode());
        Object $Y = this.getY();
        result = result * 59 + ($Y == null ? 43 : $Y.hashCode());
        Object $c = this.getC();
        result = result * 59 + ($c == null ? 43 : $c.hashCode());
        Object $sx = this.getSx();
        result = result * 59 + ($sx == null ? 43 : $sx.hashCode());
        Object $sy = this.getSy();
        result = result * 59 + ($sy == null ? 43 : $sy.hashCode());
        Object $G1Curve = this.getG1Curve();
        result = result * 59 + ($G1Curve == null ? 43 : $G1Curve.hashCode());
        return result;
    }

    public String toString() {
        return "EcdaaTrustAnchor(X=" + this.getX() + ", Y=" + this.getY() + ", c=" + this.getC() + ", sx=" + this.getSx() + ", sy=" + this.getSy() + ", G1Curve=" + this.getG1Curve() + ")";
    }
}
