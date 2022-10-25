package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.thi.bachelorthesis.fido2.rpserver.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdapterServerResponse {
    private Status status;
    private String errorMessage;
    private String sessionId;
}
