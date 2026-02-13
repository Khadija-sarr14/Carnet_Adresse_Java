package com.groupe.carnetadresse;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ContactRepository contactRepository;

    public DataLoader(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        contactRepository.save(new Contact(
                "Sarr",
                "Khadija",
                "khadija@gmail.com",
                "771234567"
        ));

        contactRepository.save(new Contact(
                "Seck",
                "Awa",
                "awa@gmail.com",
                "781234567"
        ));

        System.out.println("Données insérées avec succès !");
    }
}
