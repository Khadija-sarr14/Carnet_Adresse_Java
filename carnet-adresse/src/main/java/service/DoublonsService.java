package service;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la détection et la fusion des contacts en double
 *
 * @author Personne 5
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoublonsService {

    // ================================================================
    // ⚠️ À MODIFIER quand vous recevez le code de la Partie 1 et 2
    // ================================================================
    private final ContactRepository contactRepository;

    /**
     * Détecte les contacts potentiellement en double
     * Critères : même nom + prénom similaires OU même téléphone
     *
     * @return Liste de paires de contacts similaires
     */
    public List<PaireDoublons> detecterDoublons() {
        // ================================================================
        // ⚠️ À MODIFIER avec ContactService.listerTousLesContacts() (Partie 2)
        // ================================================================
        List<Contact> contacts = contactRepository.findAll();
        List<PaireDoublons> doublons = new ArrayList<>();

        // Comparer chaque contact avec tous les autres
        for (int i = 0; i < contacts.size(); i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                Contact c1 = contacts.get(i);
                Contact c2 = contacts.get(j);

                double score = calculerSimilarite(c1, c2);

                // Si score de similarité > 70%, c'est un doublon potentiel
                if (score > 0.70) {
                    doublons.add(new PaireDoublons(c1, c2, score));
                    log.info("Doublon détecté : {} {} et {} {} (score: {}%)",
                        c1.getNom(), c1.getPrenom(),
                        c2.getNom(), c2.getPrenom(),
                        Math.round(score * 100)
                    );
                }
            }
        }

        return doublons;
    }

    /**
     * Fusionne deux contacts en un seul
     * Le contact source (id2) sera supprimé
     * Le contact cible (id1) sera mis à jour avec les données manquantes
     *
     * @param id1 ID du contact à garder (cible)
     * @param id2 ID du contact à supprimer (source)
     * @return Contact fusionné
     */
    public Contact fusionnerContacts(Long id1, Long id2) {
        // ================================================================
        // ⚠️ À MODIFIER avec ContactService (Partie 2)
        // ================================================================
        Contact contactCible = contactRepository.findById(id1)
            .orElseThrow(() -> new RuntimeException("Contact non trouvé : " + id1));

        Contact contactSource = contactRepository.findById(id2)
            .orElseThrow(() -> new RuntimeException("Contact non trouvé : " + id2));

        // Compléter les champs vides du contact cible avec les données du source
        if (estVide(contactCible.getTelephone()) && !estVide(contactSource.getTelephone())) {
            contactCible.setTelephone(contactSource.getTelephone());
        }
        if (estVide(contactCible.getAdresse()) && !estVide(contactSource.getAdresse())) {
            contactCible.setAdresse(contactSource.getAdresse());
        }
        if (estVide(contactCible.getVille()) && !estVide(contactSource.getVille())) {
            contactCible.setVille(contactSource.getVille());
        }
        if (estVide(contactCible.getPays()) && !estVide(contactSource.getPays())) {
            contactCible.setPays(contactSource.getPays());
        }

        // Sauvegarder le contact fusionné
        Contact contactFusionne = contactRepository.save(contactCible);

        // Supprimer le contact source
        contactRepository.deleteById(id2);

        log.info("Contacts fusionnés : {} {} (id:{}) et {} {} (id:{}) → gardé id:{}",
            contactCible.getNom(), contactCible.getPrenom(), id1,
            contactSource.getNom(), contactSource.getPrenom(), id2,
            id1
        );

        return contactFusionne;
    }

    /**
     * Calcule le score de similarité entre deux contacts (0.0 à 1.0)
     */
    private double calculerSimilarite(Contact c1, Contact c2) {
        double score = 0.0;
        int criteres = 0;

        // Comparer le nom (poids fort)
        if (c1.getNom() != null && c2.getNom() != null) {
            double simNom = similariteChaine(
                c1.getNom().toLowerCase(),
                c2.getNom().toLowerCase()
            );
            score += simNom * 0.35;
            criteres++;
        }

        // Comparer le prénom (poids fort)
        if (c1.getPrenom() != null && c2.getPrenom() != null) {
            double simPrenom = similariteChaine(
                c1.getPrenom().toLowerCase(),
                c2.getPrenom().toLowerCase()
            );
            score += simPrenom * 0.35;
            criteres++;
        }

        // Comparer le téléphone (si identique → très probablement doublon)
        if (!estVide(c1.getTelephone()) && !estVide(c2.getTelephone())) {
            if (c1.getTelephone().equals(c2.getTelephone())) {
                score += 0.30;
            }
            criteres++;
        }

        return criteres > 0 ? score : 0.0;
    }

    /**
     * Calcule la similarité entre deux chaînes (algorithme simplifié)
     * Retourne une valeur entre 0.0 (différent) et 1.0 (identique)
     */
    private double similariteChaine(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        // Distance de Levenshtein simplifiée
        int maxLen = Math.max(s1.length(), s2.length());
        int distance = distanceLevenshtein(s1, s2);

        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Calcule la distance de Levenshtein entre deux chaînes
     */
    private int distanceLevenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                               Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Vérifie si une chaîne est vide ou null
     */
    private boolean estVide(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }

    /**
     * Classe représentant une paire de contacts potentiellement en double
     */
    public static class PaireDoublons {
        private final Contact contact1;
        private final Contact contact2;
        private final double scoreSimialarite;

        public PaireDoublons(Contact contact1, Contact contact2, double scoreSimilarite) {
            this.contact1 = contact1;
            this.contact2 = contact2;
            this.scoreSimialarite = scoreSimilarite;
        }

        public Contact getContact1() { return contact1; }
        public Contact getContact2() { return contact2; }
        public double getScoreSimilarite() { return scoreSimialarite; }
        public int getPourcentageSimilarite() { return (int) Math.round(scoreSimialarite * 100); }
    }
}
