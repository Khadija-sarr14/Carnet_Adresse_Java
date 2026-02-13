package controller;

import service.ImportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'import et l'export des contacts
 *
 * Endpoints disponibles :
 * POST /api/contacts/import       - Importer des contacts depuis un CSV
 * GET  /api/contacts/export/csv   - Exporter les contacts en CSV
 *
 * @author Personne 5
 * @version 1.0
 */
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class ImportExportController {

    private final ImportExportService importExportService;

    /**
     * Importer des contacts depuis un fichier CSV
     *
     * Utilisation :
     * POST /api/contacts/import
     * Content-Type: multipart/form-data
     * Body: file = votre_fichier.csv
     *
     * Format CSV attendu :
     * nom,prenom,email,telephone,adresse,ville,pays
     * Diop,Moussa,moussa@gmail.com,+221771234567,Rue 10,Dakar,Sénégal
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importerContacts(
            @RequestParam("file") MultipartFile file) {

        log.info("Import CSV demandé - Fichier : {}, Taille : {} bytes",
            file.getOriginalFilename(),
            file.getSize()
        );

        Map<String, Object> response = new HashMap<>();

        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            response.put("succes", false);
            response.put("message", "Le fichier est vide");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ImportExportService.ImportResult resultat = importExportService.importerCsv(file);

            response.put("succes", true);
            response.put("message", resultat.toString());
            response.put("importes", resultat.getImportes());
            response.put("ignores", resultat.getIgnores());
            response.put("erreurs", resultat.getErreurs());
            response.put("total", resultat.getTotal());
            response.put("details", resultat.getMessages());

            log.info("Import terminé : {}", resultat);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IOException e) {
            response.put("succes", false);
            response.put("message", "Erreur lors de la lecture du fichier : " + e.getMessage());
            log.error("Erreur d'import CSV : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Exporter tous les contacts en fichier CSV
     *
     * Utilisation :
     * GET /api/contacts/export/csv
     * → Télécharge un fichier contacts_2026-02-05.csv
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exporterContactsCsv() {
        log.info("Export CSV demandé");

        try {
            String contenuCsv = importExportService.exporterCsv();

            // Nom du fichier avec la date
            String dateActuelle = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nomFichier = "contacts_" + dateActuelle + ".csv";

            // Configurer les en-têtes de la réponse
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", nomFichier);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");

            byte[] contenu = contenuCsv.getBytes("UTF-8");

            log.info("Export CSV généré : {} bytes", contenu.length);
            return ResponseEntity.ok()
                .headers(headers)
                .body(contenu);

        } catch (Exception e) {
            log.error("Erreur lors de l'export CSV : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
