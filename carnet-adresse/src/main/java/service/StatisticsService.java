package service;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour le calcul des statistiques du carnet d'adresses
 *
 * @author Personne 5
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    // ================================================================
    // ⚠️ À MODIFIER quand vous recevez le code de la Partie 1 et 2
    // ================================================================
    private final ContactRepository contactRepository;

    /**
     * Calcule toutes les statistiques du carnet d'adresses
     *
     * @return Objet contenant toutes les statistiques
     */
    public StatisticsResult calculerStatistiques() {
        // ================================================================
        // ⚠️ À MODIFIER avec ContactService.listerTousLesContacts() (Partie 2)
        // ================================================================
        List<Contact> contacts = contactRepository.findAll();

        log.info("Calcul des statistiques pour {} contacts", contacts.size());

        // Nombre total de contacts
        long total = contacts.size();

        // Répartition par ville
        Map<String, Long> parVille = contacts.stream()
            .filter(c -> c.getVille() != null && !c.getVille().isEmpty())
            .collect(Collectors.groupingBy(Contact::getVille, Collectors.counting()));

        // Répartition par pays
        Map<String, Long> parPays = contacts.stream()
            .filter(c -> c.getPays() != null && !c.getPays().isEmpty())
            .collect(Collectors.groupingBy(Contact::getPays, Collectors.counting()));

        // Contacts avec téléphone
        long avecTelephone = contacts.stream()
            .filter(c -> c.getTelephone() != null && !c.getTelephone().isEmpty())
            .count();

        // Contacts avec adresse
        long avecAdresse = contacts.stream()
            .filter(c -> c.getAdresse() != null && !c.getAdresse().isEmpty())
            .count();

        // Ville la plus représentée
        String villePlusRepresentee = parVille.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucune");

        // Pays le plus représenté
        String paysPlusRepresente = parPays.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucun");

        // Top 5 villes
        Map<String, Long> top5Villes = parVille.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        // Top 5 pays
        Map<String, Long> top5Pays = parPays.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        return new StatisticsResult(
            total,
            top5Villes,
            top5Pays,
            avecTelephone,
            avecAdresse,
            villePlusRepresentee,
            paysPlusRepresente
        );
    }

    /**
     * Classe représentant les résultats des statistiques
     */
    public static class StatisticsResult {
        private final long totalContacts;
        private final Map<String, Long> contactsParVille;
        private final Map<String, Long> contactsParPays;
        private final long avecTelephone;
        private final long avecAdresse;
        private final String villePlusRepresentee;
        private final String paysPlusRepresente;

        public StatisticsResult(
                long totalContacts,
                Map<String, Long> contactsParVille,
                Map<String, Long> contactsParPays,
                long avecTelephone,
                long avecAdresse,
                String villePlusRepresentee,
                String paysPlusRepresente) {
            this.totalContacts = totalContacts;
            this.contactsParVille = contactsParVille;
            this.contactsParPays = contactsParPays;
            this.avecTelephone = avecTelephone;
            this.avecAdresse = avecAdresse;
            this.villePlusRepresentee = villePlusRepresentee;
            this.paysPlusRepresente = paysPlusRepresente;
        }

        public long getTotalContacts() { return totalContacts; }
        public Map<String, Long> getContactsParVille() { return contactsParVille; }
        public Map<String, Long> getContactsParPays() { return contactsParPays; }
        public long getAvecTelephone() { return avecTelephone; }
        public long getAvecAdresse() { return avecAdresse; }
        public String getVillePlusRepresentee() { return villePlusRepresentee; }
        public String getPaysPlusRepresente() { return paysPlusRepresente; }
    }
}
