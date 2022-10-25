package de.thi.bachelorthesis.fido2.rpserver.general.uaf;

public class ExtensionDescriptor {
    private String id;
    private String data;
    private boolean fail_if_unknown;

    public ExtensionDescriptor() {
    }

    public String getId() {
        return this.id;
    }

    public String getData() {
        return this.data;
    }

    public boolean isFail_if_unknown() {
        return this.fail_if_unknown;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFail_if_unknown(boolean fail_if_unknown) {
        this.fail_if_unknown = fail_if_unknown;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ExtensionDescriptor)) {
            return false;
        } else {
            ExtensionDescriptor other = (ExtensionDescriptor)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label39: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label39;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label39;
                    }

                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                if (this.isFail_if_unknown() != other.isFail_if_unknown()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExtensionDescriptor;
    }

    public int hashCode() {
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        result = result * 59 + (this.isFail_if_unknown() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "ExtensionDescriptor(id=" + this.getId() + ", data=" + this.getData() + ", fail_if_unknown=" + this.isFail_if_unknown() + ")";
    }
}
