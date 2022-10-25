package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmCapVendorId {
    AMD(0x414D4400, "AMD"),
    ATMEL(0x41544D4C, "Atmel"),
    BROADCOM(0x4252434D, "Broadcom"),
    HPE(0x48504500, "HPE"),
    IBM(0x49424D00, "IBM"),
    INFINEON(0x49465800, "Infineon"),
    INTEL(0x494E5443, "Intel"),
    LENOVO(0x4C454E00 ,"Lenovo"),
    MICROSOFT(0x4D534654, "Microsoft"),
    NATIONAL_SEMI(0x4E534D20, "National Semi"),
    NUVOTON_TECHNOLOGY(0x4E544300, "Nuvoton Technology"),
    NATIONZ(0x4E545A00, "Nationz"),
    QUALCOMM(0x51434F4D, "Qualcomm"),
    SMSC(0x534D5343, "SMSC"),
    ST_MICROELECTRONICS(0x53544D20, "STMicroelectronics"),
    SAMSUNG(0x534D534E, "Samsung"),
    SINOSUN(0x534E5300, "Sinosun"),
    TEXAS_INSTRUMENTS(0x54584E00, "Texas Instruments"),
    WINDBOND(0x57454300, "Windbond"),
    FUZHOU_ROCKCHIP(0x524F4343, "Fuzhou Rockchip"),
    GOOGLE(0x474F4F47, "Google");

    @Getter
    private final long value;

    @Getter
    private final String name;

    public static TpmCapVendorId fromValue(long value) {
        return Arrays.stream(TpmCapVendorId.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
