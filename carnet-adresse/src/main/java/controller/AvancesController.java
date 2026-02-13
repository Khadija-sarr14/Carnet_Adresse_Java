package controller;

import com.groupe.carnetadresse.entity.Contact;
import com.groupe.carnetadresse.repository.ContactRepository;
import service.DoublonsService;
import service.FavorisService;
import specification.ContactSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour les fonctionnalités avancées :
 * - Pagination et tri
 * - Filtres avancés
 * - Favoris
 * - Détection et fusion des doublons
 *
 * @author Personne 5
 * @version 1.0
 */
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class AvancesController {

    // ================================================================
    // ⚠️ À MODIFIER quand vous recevez le code de la Partie 1 et 3
    // Remplacez ContactRepository par ContactService si disponible
    // ================================================================
    private final ContactRepository contactRepository;
    private final FavorisService favorisService;
    private final DoublonsService doublonsService;

    // ================================================================
    // 1. PAGINATION ET TRI
    // ================================================================

    /**
     * Lister les contacts avec pagination et tri
     *
     * Exemples d'utilisation :
     * GET /api/contacts/page?page=0&size=20
     * GET /api/contacts/page?page=0&size=20&sort=nom&direction=asc
     * GET /api/contacts/page?page=1&size=10&sort=dateCreation&direction=desc
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> listerContactsPagines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nom") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("Pagination : page={}, size={}, sort={}, direction={}", page, size, sort, direction);

        // Configurer le tri
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        // Créer la configuration de pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // ================================================================
        // ⚠️ À MODIFIER avec JpaSpecificationExecutor (Partie 1)
        // ================================================================
        Page<Contact> pageContacts = contactRepository.findAll(pageable);

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("contacts", pageContacts.getContent());
        response.put("pageActuelle", pageContacts.getNumber());
        response.put("totalPages", pageContacts.getTotalPages());
        response.put("totalElements", pageContacts.getTotalElements());
        response.put("taillePage", pageContacts.getSize());
        response.put("premierePage", pageContacts.isFirst());
        response.put("dernierePage", pageContacts.isLast());

        return ResponseEntity.ok(response);
    }

    // ================================================================
    // 2. FILTRES AVANCÉS
    // ================================================================

    /**
     * Rechercher des contacts avec plusieurs filtres combinés
     *
     * Exemples d'utilisation :
     * GET /api/contacts/filtrer?ville=Dakar
     * GET /api/contacts/filtrer?pays=Sénégal
     * GET /api/contacts/filtrer?ville=Dakar&pays=Sénégal
     * GET /api/contacts/filtrer?nom=Diop&ville=Dakar
     * GET /api/contacts/filtrer?page=0&size=10&ville=Dakar&sort=nom&direction=asc
     */
    @GetMapping("/filtrer")
    public ResponseEntity<Map<String, Object>> filtrerContacts(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String pays,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nom") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("Filtre avancé : nom={}, ville={}, pays={}", nom, ville, pays);

        // Créer la spécification de filtrage
        Specification<Contact> spec = ContactSpecification.filtrerContacts(
            nom, prenom, email, telephone, ville, pays
        );

        // Configurer la pagination
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // ================================================================
        // ⚠️ À MODIFIER : ContactRepository doit implémenter
        // JpaSpecificationExecutor<Contact> (Partie 1)
        // ================================================================
        Page<Contact> pageContacts = contactRepository.findAll(spec, pageable);

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("contacts", pageContacts.getContent());
        response.put("pageActuelle", pageContacts.getNumber());
        response.put("totalPages", pageContacts.getTotalPages());
        response.put("totalElements", pageContacts.getTotalElements());
        response.put("taillePage", pageContacts.getSize());
        response.put("filtresAppliques", construireFiltresAppliques(
            nom, prenom, email, telephone, ville, pays
        ));

        return ResponseEntity.ok(response);
    }

    // ================================================================
    // 3. FAVORIS
    // ================================================================

    /**
     * Obtenir tous les contacts favoris
     *
     * GET /api/contacts/favoris
     */
    @GetMapping("/favoris")
    public ResponseEntity<Map<String, Object>> obtenirFavoris() {
        log.info("Récupération des favoris");

        List<Contact> favoris = favorisService.obtenirFavoris();

        Map<String, Object> response = new HashMap<>();
        response.put("favoris", favoris);
        response.put("total", favoris.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Basculer le statut favori d'un contact
     *
     * PUT /api/contacts/{id}/toggle-favori
     */
    @PutMapping("/{id}/toggle-favori")
    public ResponseEntity<Map<String, Object>> toggleFavori(@PathVariable Long id) {
        log.info("Toggle favori pour contact id={}", id);

        Contact contact = favorisService.toggleFavori(id);

        Map<String, Object> response = new HashMap<>();
        response.put("contact", contact);
        response.put("estFavori", contact.isFavori());
        response.put("message", contact.isFavori()
            ? "Contact ajouté aux favoris ❤️"
            : "Contact retiré des favoris"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Compter les contacts favoris
     *
     * GET /api/contacts/favoris/count
     */
    @GetMapping("/favoris/count")
    public ResponseEntity<Map<String, Object>> compterFavoris() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalFavoris", favorisService.compterFavoris());
        return ResponseEntity.ok(response);
    }

    // ================================================================
    // 4. DÉTECTION ET FUSION DES DOUBLONS
    // ================================================================

    /**
     * Détecter les contacts potentiellement en double
     *
     * GET /api/contacts/duplicates
     */
    @GetMapping("/duplicates")
    public ResponseEntity<Map<String, Object>> detecterDoublons() {
        log.info("Détection des doublons demandée");

        List<DoublonsService.PaireDoublons> doublons = doublonsService.detecterDoublons();

        Map<String, Object> response = new HashMap<>();
        response.put("doublons", doublons);
        response.put("nombrePairesDetectees", doublons.size());
        response.put("message", doublons.isEmpty()
            ? "Aucun doublon détecté ✅"
            : doublons.size() + " paire(s) de doublons potentiels détectée(s)"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Fusionner deux contacts en un seul
     *
     * POST /api/contacts/merge/{id1}/{id2}
     * - id1 : Contact à garder
     * - id2 : Contact à supprimer
     */
    @PostMapping("/merge/{id1}/{id2}")
    public ResponseEntity<Map<String, Object>> fusionnerContacts(
            @PathVariable Long id1,
            @PathVariable Long id2) {

        log.info("Fusion des contacts id={} et id={}", id1, id2);

        Contact contactFusionne = doublonsService.fusionnerContacts(id1, id2);

        Map<String, Object> response = new HashMap<>();
        response.put("contact", contactFusionne);
        response.put("message", "Contacts fusionnés avec succès ✅");

        return ResponseEntity.ok(response);
    }

    // ================================================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ================================================================

    /**
     * Construit un résumé des filtres appliqués pour la réponse
     */
    private Map<String, String> construireFiltresAppliques(
            String nom, String prenom, String email,
            String telephone, String ville, String pays) {

        Map<String, String> filtres = new HashMap<>();
        if (nom != null && !nom.isEmpty()) filtres.put("nom", nom);
        if (prenom != null && !prenom.isEmpty()) filtres.put("prenom", prenom);
        if (email != null && !email.isEmpty()) filtres.put("email", email);
        if (telephone != null && !telephone.isEmpty()) filtres.put("telephone", telephone);
        if (ville != null && !ville.isEmpty()) filtres.put("ville", ville);
        if (pays != null && !pays.isEmpty()) filtres.put("pays", pays);
        return filtres;
    }
}
