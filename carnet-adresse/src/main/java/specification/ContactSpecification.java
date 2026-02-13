package specification;

import com.groupe.carnetadresse.entity.Contact;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Spécifications JPA pour les filtres avancés de recherche de contacts
 * Permet de combiner plusieurs critères de recherche dynamiquement
 *
 * @author Personne 5
 * @version 1.0
 */
public class ContactSpecification {

    /**
     * Filtre les contacts selon plusieurs critères combinés
     *
     * @param nom       Filtre par nom (optionnel)
     * @param prenom    Filtre par prénom (optionnel)
     * @param email     Filtre par email (optionnel)
     * @param telephone Filtre par téléphone (optionnel)
     * @param ville     Filtre par ville (optionnel)
     * @param pays      Filtre par pays (optionnel)
     * @return Specification JPA avec tous les filtres combinés
     */
    public static Specification<Contact> filtrerContacts(
            String nom,
            String prenom,
            String email,
            String telephone,
            String ville,
            String pays) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre par nom
            if (nom != null && !nom.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nom")),
                    "%" + nom.toLowerCase() + "%"
                ));
            }

            // Filtre par prénom
            if (prenom != null && !prenom.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("prenom")),
                    "%" + prenom.toLowerCase() + "%"
                ));
            }

            // Filtre par email
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
                ));
            }

            // Filtre par téléphone
            if (telephone != null && !telephone.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("telephone"),
                    "%" + telephone + "%"
                ));
            }

            // Filtre par ville
            if (ville != null && !ville.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("ville")),
                    "%" + ville.toLowerCase() + "%"
                ));
            }

            // Filtre par pays
            if (pays != null && !pays.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("pays")),
                    "%" + pays.toLowerCase() + "%"
                ));
            }

            // Combiner tous les filtres avec AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Recherche globale dans tous les champs texte
     *
     * @param query Terme de recherche
     * @return Specification JPA pour la recherche globale
     */
    public static Specification<Contact> rechercheGlobale(String query) {
        return (root, q, criteriaBuilder) -> {
            if (query == null || query.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + query.toLowerCase() + "%";

            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("telephone")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("ville")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("pays")), pattern)
            );
        };
    }
}
