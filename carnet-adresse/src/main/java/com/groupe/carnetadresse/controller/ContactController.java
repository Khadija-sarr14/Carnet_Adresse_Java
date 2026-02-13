package com.groupe.carnetadresse.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupe.carnetadresse.dto.ContactDTO;
import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.mapper.ContactMapper;
import com.groupe.carnetadresse.repository.ContactRepository;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    @Autowired
    private ContactRepository contactRepository; // Connexion au code de P2
    @Autowired
    private ContactMapper contactMapper;

    @GetMapping
    public List<ContactDTO> getAll() {
        return contactRepository.findAll().stream()
                .map(contactMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ContactDTO create(@RequestBody ContactDTO dto) {
        Contact contact = contactMapper.toEntity(dto);
        return contactMapper.toDTO(contactRepository.save(contact));
    }
}
