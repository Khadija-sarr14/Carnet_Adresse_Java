package controller;

import service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour les statistiques du carnet d'adresses
 *
 * Endpoints disponibles :
 * GET /api/contacts/stats - Obtenir toutes les statistiques
 *
 * @author Personne 5
 * @version 1.0
 */
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Obtenir les statistiques complètes du carnet d'adresses
     *
     * Utilisation :
     * GET /api/contacts/stats
     *
     * Résultat :
     * {
     *   "totalContacts": 150,
     *   "contactsParVille": {"Dakar": 80, "Thiès": 40},
     *   "contactsParPays": {"Sénégal": 120},
     *   "avecTelephone": 130,
     *   "avecAdresse": 100,
     *   "villePlusRepresentee": "Dakar",
     *   "paysPlusRepresente": "Sénégal"
     * }
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obtenirStatistiques() {
        log.info("Statistiques demandées");

        StatisticsService.StatisticsResult stats = statisticsService.calculerStatistiques();

        Map<String, Object> response = new HashMap<>();
        response.put("totalContacts", stats.getTotalContacts());
        response.put("contactsParVille", stats.getContactsParVille());
        response.put("contactsParPays", stats.getContactsParPays());
        response.put("avecTelephone", stats.getAvecTelephone());
        response.put("avecAdresse", stats.getAvecAdresse());
        response.put("villePlusRepresentee", stats.getVillePlusRepresentee());
        response.put("paysPlusRepresente", stats.getPaysPlusRepresente());

        // Pourcentages
        if (stats.getTotalContacts() > 0) {
            response.put("pourcentageAvecTelephone",
                Math.round((double) stats.getAvecTelephone() / stats.getTotalContacts() * 100));
            response.put("pourcentageAvecAdresse",
                Math.round((double) stats.getAvecAdresse() / stats.getTotalContacts() * 100));
        }

        return ResponseEntity.ok(response);
    }
}
