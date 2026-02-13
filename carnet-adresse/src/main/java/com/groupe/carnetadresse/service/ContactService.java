package com.groupe.carnetadresse.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public void createContact(Contact contact) {
        contactRepository.save(contact);
    }
}