package com.groupe.carnetadresse.mapper;

import org.springframework.stereotype.Component;

import com.groupe.carnetadresse.dto.ContactDTO;
import com.groupe.carnetadresse.entity.Contact;

@Component
public class ContactMapper {
    public ContactDTO toDTO(Contact contact) {
        if (contact == null) return null;
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setNom(contact.getNom());
        dto.setPrenom(contact.getPrenom());
        dto.setEmail(contact.getEmail());
        dto.setTelephone(contact.getTelephone());
        return dto;
    }

    public Contact toEntity(ContactDTO dto) {
        if (dto == null) return null;
        return new Contact(dto.getNom(), dto.getPrenom(), dto.getEmail(), dto.getTelephone());
    }
}
