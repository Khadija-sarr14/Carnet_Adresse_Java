package util;

import com.groupe.carnetadresse.entity.Contact;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire pour la gestion des fichiers CSV
 * Import et Export des contacts
 *
 * @author Personne 5
 * @version 1.0
 */
public class CsvUtil {

    // En-têtes du fichier CSV
    public static final String[] CSV_HEADERS = {
        "id", "nom", "prenom", "email", "telephone", "adresse", "ville", "pays"
    };

    public static final String CSV_SEPARATOR = ",";

    /**
     * Vérifie si le fichier uploadé est bien un CSV
     *
     * @param file Fichier uploadé
     * @return true si c'est un CSV, false sinon
     */
    public static boolean estFichierCsv(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("text/csv") ||
            contentType.equals("application/csv") ||
            contentType.equals("application/vnd.ms-excel") ||
            file.getOriginalFilename() != null &&
            file.getOriginalFilename().endsWith(".csv")
        );
    }

    /**
     * Parse un fichier CSV et retourne une liste de contacts
     *
     * @param file Fichier CSV uploadé
     * @return Liste des contacts parsés
     * @throws IOException En cas d'erreur de lecture
     */
    public static List<Contact> parseCsv(MultipartFile file) throws IOException {
        List<Contact> contacts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String ligne;
            int numeroLigne = 0;

            while ((ligne = reader.readLine()) != null) {
                numeroLigne++;

                // Ignorer la première ligne (en-têtes)
                if (numeroLigne == 1) continue;

                // Ignorer les lignes vides
                if (ligne.trim().isEmpty()) continue;

                // Parser la ligne
                Contact contact = parseLigneCsv(ligne);
                if (contact != null) {
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    /**
     * Parse une ligne CSV et retourne un Contact
     *
     * @param ligne Ligne CSV à parser
     * @return Contact parsé ou null si la ligne est invalide
     */
    private static Contact parseLigneCsv(String ligne) {
        try {
            // Gestion des virgules dans les champs entre guillemets
            String[] colonnes = ligne.split(CSV_SEPARATOR, -1);

            // Vérifier qu'il y a au moins nom, prenom et email
            if (colonnes.length < 3) return null;

            Contact contact = new Contact();
            contact.setNom(nettoyerValeur(colonnes[0]));
            contact.setPrenom(nettoyerValeur(colonnes[1]));
            contact.setEmail(nettoyerValeur(colonnes[2]));

            // Champs optionnels
            if (colonnes.length > 3) contact.setTelephone(nettoyerValeur(colonnes[3]));
            if (colonnes.length > 4) contact.setAdresse(nettoyerValeur(colonnes[4]));
            if (colonnes.length > 5) contact.setVille(nettoyerValeur(colonnes[5]));
            if (colonnes.length > 6) contact.setPays(nettoyerValeur(colonnes[6]));

            return contact;

        } catch (Exception e) {
            // Ligne invalide, on l'ignore
            return null;
        }
    }

    /**
     * Génère le contenu CSV à partir d'une liste de contacts
     *
     * @param contacts Liste des contacts à exporter
     * @return Contenu CSV sous forme de String
     */
    public static String genererCsv(List<Contact> contacts) {
        StringBuilder sb = new StringBuilder();

        // Ajouter les en-têtes
        sb.append("nom,prenom,email,telephone,adresse,ville,pays\n");

        // Ajouter chaque contact
        for (Contact contact : contacts) {
            sb.append(echapperValeur(contact.getNom())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getPrenom())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getEmail())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getTelephone())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getAdresse())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getVille())).append(CSV_SEPARATOR);
            sb.append(echapperValeur(contact.getPays())).append("\n");
        }

        return sb.toString();
    }

    /**
     * Nettoie une valeur CSV (enlève les guillemets, espaces)
     */
    private static String nettoyerValeur(String valeur) {
        if (valeur == null) return "";
        valeur = valeur.trim();
        if (valeur.startsWith("\"") && valeur.endsWith("\"")) {
            valeur = valeur.substring(1, valeur.length() - 1);
        }
        return valeur;
    }

    /**
     * Échappe une valeur pour le CSV (ajoute des guillemets si nécessaire)
     */
    private static String echapperValeur(String valeur) {
        if (valeur == null) return "";
        if (valeur.contains(",") || valeur.contains("\"") || valeur.contains("\n")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }
}
