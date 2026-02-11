package com.groupe.carnetadresse.repository;

import com.groupe.carnetadresse.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Recherche par nom
    List<Contact> findByNom(String nom);

    // Recherche par email
    Contact findByEmail(String email);
}
