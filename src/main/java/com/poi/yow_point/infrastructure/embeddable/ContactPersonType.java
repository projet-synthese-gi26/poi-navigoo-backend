package com.poi.yow_point.infrastructure.embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPersonType {

    private String name;
    private String role;
    private String phone;
    private String email;

    // Méthode utilitaire pour obtenir le contact formaté
    public String getFormattedContact() {
        StringBuilder contact = new StringBuilder();

        if (name != null && !name.trim().isEmpty()) {
            contact.append(name);
        }
        if (role != null && !role.trim().isEmpty()) {
            if (contact.length() > 0) {
                contact.append(" (").append(role).append(")");
            } else {
                contact.append(role);
            }
        }

        return contact.toString().trim();
    }

    // Méthode utilitaire pour vérifier si le contact est vide
    public boolean isEmpty() {
        return (name == null || name.trim().isEmpty()) &&
                (role == null || role.trim().isEmpty()) &&
                (phone == null || phone.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty());
    }

    // Méthode utilitaire pour vérifier si le contact a au moins un moyen de
    // communication
    public boolean hasContactInfo() {
        return (phone != null && !phone.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty());
    }

    // Méthode utilitaire pour obtenir le moyen de contact préféré
    public String getPreferredContact() {
        if (email != null && !email.trim().isEmpty()) {
            return email;
        } else if (phone != null && !phone.trim().isEmpty()) {
            return phone;
        }
        return null;
    }
}