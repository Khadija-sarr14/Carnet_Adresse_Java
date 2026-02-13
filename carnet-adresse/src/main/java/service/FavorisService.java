package service;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service pour la gestion des contacts favoris
 *
 * @author Personne 5
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FavorisService {

    // ================================================================
    // ⚠️ À MODIFIER quand vous recevez le code de la Partie 1 et 2
    // ================================================================
    private final ContactRepository contactRepository;

    /**
     * Retourne tous les contacts marqués comme favoris
     *
     * @return Liste des contacts favoris
     */
    public List<Contact> obtenirFavoris() {
        // ================================================================
        // ⚠️ À MODIFIER avec la méthode findByFavoriTrue() dans
        // ContactRepository (Partie 1)
        // ================================================================
        log.info("Récupération des contacts favoris");
        return contactRepository.findByFavoriTrue();
    }

    /**
     * Bascule le statut favori d'un contact
     * Si favori → non favori
     * Si non favori → favori
     *
     * @param id Identifiant du contact
     * @return Contact mis à jour
     */
    public Contact toggleFavori(Long id) {
        // ================================================================
        // ⚠️ À MODIFIER avec ContactService.obtenirContact(id) (Partie 2)
        // ================================================================
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact non trouvé avec l'id : " + id));

        // Basculer le statut favori
        contact.setFavori(!contact.isFavori());
        Contact contactMisAJour = contactRepository.save(contact);

        log.info("Contact {} {} : favori = {}",
            contact.getNom(),
            contact.getPrenom(),
            contactMisAJour.isFavori()
        );

        return contactMisAJour;
    }

    /**
     * Vérifie si un contact est favori
     *
     * @param id Identifiant du contact
     * @return true si le contact est favori
     */
    public boolean estFavori(Long id) {
        return contactRepository.findById(id)
            .map(Contact::isFavori)
            .orElse(false);
    }

    /**
     * Retourne le nombre de contacts favoris
     *
     * @return Nombre de favoris
     */
    public long compterFavoris() {
        return contactRepository.countByFavoriTrue();
    }
}
