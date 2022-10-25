package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceInfo {
    private String deviceId;
    private String displayName;
    private String imageUrl;
    private String deviceUrl;
    private Integer transports;
    private List<Selector> selectors;

    public List<Transports> getTransports() {
        List<Transports> transportList = new ArrayList<>();
        if (transports != null) {
            for (Transports t : Transports.values()) {
                if ((transports & t.getValue()) == t.getValue()) {
                    transportList.add(t);
                }
            }
        }
        return transportList;
    }
}
