package com.poi.yow_point.infrastructure.embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressType {

    private String streetNumber;
    private String streetName;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private String informalAddress;

    // Méthode utilitaire pour obtenir l'adresse complète formatée
    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();

        if (streetNumber != null && !streetNumber.trim().isEmpty()) {
            address.append(streetNumber).append(" ");
        }
        if (streetName != null && !streetName.trim().isEmpty()) {
            address.append(streetName).append(", ");
        }
        if (city != null && !city.trim().isEmpty()) {
            address.append(city);
        }
        if (stateProvince != null && !stateProvince.trim().isEmpty()) {
            address.append(", ").append(stateProvince);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            address.append(" ").append(postalCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            address.append(", ").append(country);
        }

        return address.toString().trim();
    }

    // Méthode utilitaire pour vérifier si l'adresse est vide
    public boolean isEmpty() {
        return (streetNumber == null || streetNumber.trim().isEmpty()) &&
                (streetName == null || streetName.trim().isEmpty()) &&
                (city == null || city.trim().isEmpty()) &&
                (stateProvince == null || stateProvince.trim().isEmpty()) &&
                (postalCode == null || postalCode.trim().isEmpty()) &&
                (country == null || country.trim().isEmpty()) &&
                (informalAddress == null || informalAddress.trim().isEmpty());
    }
}