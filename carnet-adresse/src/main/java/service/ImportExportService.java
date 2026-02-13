package service;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import com.groupe.carnetadresse.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour l'import et l'export des contacts
 *
 * @author Personne 5
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportService {

    // ================================================================
    // ⚠️ À MODIFIER quand vous recevez le code de la Partie 1 et 2
    // Remplacez ContactRepository par ContactService si nécessaire
    // ================================================================
    private final ContactRepository contactRepository;

    /**
     * Importe des contacts depuis un fichier CSV
     *
     * @param file Fichier CSV uploadé
     * @return Résultat de l'import avec statistiques
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public ImportResult importerCsv(MultipartFile file) throws IOException {
        // Vérifier que le fichier est un CSV
        if (!CsvUtil.estFichierCsv(file)) {
            throw new IllegalArgumentException(
                "Le fichier doit être au format CSV. Fichier reçu : " +
                file.getOriginalFilename()
            );
        }

        // Parser le fichier CSV
        List<Contact> contactsAImporter = CsvUtil.parseCsv(file);

        int importes = 0;
        int ignores = 0;
        int erreurs = 0;
        List<String> messages = new ArrayList<>();

        // Sauvegarder chaque contact
        for (Contact contact : contactsAImporter) {
            try {
                // Vérifier si l'email existe déjà
                // ================================================================
                // ⚠️ À MODIFIER avec la méthode de ContactRepository (Partie 1)
                // ================================================================
                if (contactRepository.existsByEmail(contact.getEmail())) {
                    ignores++;
                    messages.add("Contact ignoré (email existe déjà) : " + contact.getEmail());
                    continue;
                }

                // Sauvegarder le contact
                contactRepository.save(contact);
                importes++;
                log.info("Contact importé : {} {}", contact.getNom(), contact.getPrenom());

            } catch (Exception e) {
                erreurs++;
                messages.add("Erreur pour " + contact.getEmail() + " : " + e.getMessage());
                log.error("Erreur lors de l'import du contact : {}", e.getMessage());
            }
        }

        return new ImportResult(importes, ignores, erreurs, messages);
    }

    /**
     * Exporte tous les contacts en format CSV
     *
     * @return Contenu CSV sous forme de String
     */
    public String exporterCsv() {
        // ================================================================
        // ⚠️ À MODIFIER avec ContactService.listerTousLesContacts() (Partie 2)
        // ================================================================
        List<Contact> contacts = contactRepository.findAll();

        log.info("Export CSV de {} contacts", contacts.size());
        return CsvUtil.genererCsv(contacts);
    }

    /**
     * Classe interne représentant le résultat d'un import
     */
    public static class ImportResult {
        private final int importes;
        private final int ignores;
        private final int erreurs;
        private final List<String> messages;

        public ImportResult(int importes, int ignores, int erreurs, List<String> messages) {
            this.importes = importes;
            this.ignores = ignores;
            this.erreurs = erreurs;
            this.messages = messages;
        }

        public int getImportes() { return importes; }
        public int getIgnores() { return ignores; }
        public int getErreurs() { return erreurs; }
        public List<String> getMessages() { return messages; }
        public int getTotal() { return importes + ignores + erreurs; }

        @Override
        public String toString() {
            return String.format(
                "Import terminé : %d importés, %d ignorés, %d erreurs sur %d total",
                importes, ignores, erreurs, getTotal()
            );
        }
    }
}
