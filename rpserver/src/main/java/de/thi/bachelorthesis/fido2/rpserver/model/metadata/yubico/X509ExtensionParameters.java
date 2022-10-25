package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class X509ExtensionParameters extends Parameters {
    private String key;
    private String value;
}
