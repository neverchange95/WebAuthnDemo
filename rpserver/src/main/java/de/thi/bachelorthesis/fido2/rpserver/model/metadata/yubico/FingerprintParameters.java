package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FingerprintParameters extends Parameters {
    private List<String> fingerprints;
}
