package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.thi.bachelorthesis.fido2.rpserver.general.TokenBinding;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectedClientData {
    private String type;
    private String challenge;
    private String origin;
    private TokenBinding tokenBinding;
    private String androidPackageName;  // Android specific e.g., com.chrome.canary
}
