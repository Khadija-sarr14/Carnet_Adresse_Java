package com.gestion.contact.service;

import com.gestion.contact.entity.Contact;
import com.gestion.contact.exception.ContactNotFoundException;
import com.gestion.contact.exception.DuplicateContactException;
import com.gestion.contact.exception.InvalidContactException;
import com.gestion.contact.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation des services de gestion des contacts
 * Contient toute la logique métier de l'application
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
@Service
@Transactional
public class ContactServiceImpl implements ContactService {
    
    private final ContactRepository contactRepository;
    
    /**
     * Constructeur avec injection de dépendance
     * 
     * @param contactRepository Le repository des contacts
     */
    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }
    
    /**
     * Créer un nouveau contact
     * Vérifie que l'email n'existe pas déjà avant la création
     * 
     * @param contact Le contact à créer
     * @return Le contact créé avec son ID généré
     * @throws InvalidContactException si le contact est null
     * @throws DuplicateContactException si l'email existe déjà
     */
    @Override
    public Contact createContact(Contact contact) {
        // Validation de l'objet contact
        if (contact == null) {
            throw new InvalidContactException("Le contact ne peut pas être null");
        }
        
        // Validation des champs obligatoires
        validateContactFields(contact);
        
        // Vérifier si l'email existe déjà
        if (contact.getEmail() != null && 
            contactRepository.existsByEmail(contact.getEmail())) {
            throw DuplicateContactException.forEmail(contact.getEmail());
        }
        
        // Sauvegarder le contact
        return contactRepository.save(contact);
    }
    
    /**
     * Récupérer tous les contacts
     * 
     * @return Liste de tous les contacts
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }
    
    /**
     * Récupérer un contact par son ID
     * Lève une exception si le contact n'existe pas
     * 
     * @param id L'identifiant du contact
     * @return Le contact trouvé
     * @throws IllegalArgumentException si l'ID est null
     * @throws ContactNotFoundException si le contact n'existe pas
     */
    @Override
    @Transactional(readOnly = true)
    public Contact getContactById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        
        return contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException(id));
    }
    
    /**
     * Récupérer un contact par son email
     * 
     * @param email L'email du contact
     * @return Optional contenant le contact si trouvé
     * @throws IllegalArgumentException si l'email est null ou vide
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> getContactByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        
        return contactRepository.findByEmail(email);
    }
    
    /**
     * Rechercher des contacts par nom (recherche partielle, insensible à la casse)
     * 
     * @param nom Le nom à rechercher
     * @return Liste des contacts correspondants
     * @throws IllegalArgumentException si le nom est null ou vide
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> searchContactsByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }
        
        return contactRepository.findByNomContainingIgnoreCase(nom);
    }
    
    /**
     * Rechercher des contacts par prénom (recherche partielle, insensible à la casse)
     * 
     * @param prenom Le prénom à rechercher
     * @return Liste des contacts correspondants
     * @throws IllegalArgumentException si le prénom est null ou vide
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> searchContactsByPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de recherche ne peut pas être vide");
        }
        
        return contactRepository.findByPrenomContainingIgnoreCase(prenom);
    }
    
    /**
     * Mettre à jour un contact existant
     * Vérifie que le nouveau email n'est pas déjà utilisé par un autre contact
     * 
     * @param id L'identifiant du contact à mettre à jour
     * @param contactDetails Les nouvelles informations du contact
     * @return Le contact mis à jour
     * @throws IllegalArgumentException si l'ID ou les détails sont null
     * @throws ContactNotFoundException si le contact n'existe pas
     * @throws DuplicateContactException si l'email est déjà utilisé
     */
    @Override
    public Contact updateContact(Long id, Contact contactDetails) {
        // Validation des paramètres
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        if (contactDetails == null) {
            throw new InvalidContactException("Les détails du contact ne peuvent pas être null");
        }
        
        // Vérifier que le contact existe
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException(id));
        
        // Vérifier si l'email a changé et s'il n'est pas déjà utilisé
        if (contactDetails.getEmail() != null && 
            !contactDetails.getEmail().equals(existingContact.getEmail())) {
            
            // Vérifier si le nouvel email existe déjà
            Optional<Contact> contactWithEmail = contactRepository.findByEmail(contactDetails.getEmail());
            if (contactWithEmail.isPresent() && !contactWithEmail.get().getId().equals(id)) {
                throw DuplicateContactException.forEmail(contactDetails.getEmail());
            }
        }
        
        // Mettre à jour les champs non-null
        updateContactFields(existingContact, contactDetails);
        
        // Sauvegarder et retourner le contact mis à jour
        return contactRepository.save(existingContact);
    }
    
    /**
     * Supprimer un contact
     * Lève une exception si le contact n'existe pas
     * 
     * @param id L'identifiant du contact à supprimer
     * @throws IllegalArgumentException si l'ID est null
     * @throws ContactNotFoundException si le contact n'existe pas
     */
    @Override
    public void deleteContact(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        
        // Vérifier que le contact existe avant de le supprimer
        if (!contactRepository.existsById(id)) {
            throw new ContactNotFoundException(id);
        }
        
        contactRepository.deleteById(id);
    }
    
    /**
     * Vérifier si un email existe déjà dans la base de données
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return contactRepository.existsByEmail(email);
    }
    
    /**
     * Compter le nombre total de contacts
     * 
     * @return Le nombre de contacts
     */
    @Override
    @Transactional(readOnly = true)
    public long countContacts() {
        return contactRepository.count();
    }
    
    /**
     * Supprimer tous les contacts (à utiliser avec précaution !)
     */
    @Override
    public void deleteAllContacts() {
        contactRepository.deleteAll();
    }
    
    // ============ Méthodes privées de validation et utilitaires ============
    
    /**
     * Valider les champs obligatoires d'un contact
     * 
     * @param contact Le contact à valider
     * @throws InvalidContactException si un champ obligatoire est manquant ou invalide
     */
    private void validateContactFields(Contact contact) {
        if (contact.getNom() == null || contact.getNom().trim().isEmpty()) {
            throw InvalidContactException.forField("nom", "Le nom est obligatoire");
        }
        
        if (contact.getPrenom() == null || contact.getPrenom().trim().isEmpty()) {
            throw InvalidContactException.forField("prenom", "Le prénom est obligatoire");
        }
        
        if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
            throw InvalidContactException.forField("email", "L'email est obligatoire");
        }
        
        // Validation simple du format email
        if (!contact.getEmail().contains("@")) {
            throw InvalidContactException.forField("email", "Le format de l'email est invalide");
        }
    }
    
    /**
     * Mettre à jour les champs d'un contact existant avec les nouveaux détails
     * 
     * @param existingContact Le contact existant à modifier
     * @param newDetails Les nouvelles informations
     */
    private void updateContactFields(Contact existingContact, Contact newDetails) {
        if (newDetails.getNom() != null && !newDetails.getNom().trim().isEmpty()) {
            existingContact.setNom(newDetails.getNom());
        }
        
        if (newDetails.getPrenom() != null && !newDetails.getPrenom().trim().isEmpty()) {
            existingContact.setPrenom(newDetails.getPrenom());
        }
        
        if (newDetails.getEmail() != null && !newDetails.getEmail().trim().isEmpty()) {
            existingContact.setEmail(newDetails.getEmail());
        }
        
        // Les champs optionnels peuvent être null pour les effacer
        if (newDetails.getTelephone() != null) {
            existingContact.setTelephone(newDetails.getTelephone());
        }
        
        if (newDetails.getAdresse() != null) {
            existingContact.setAdresse(newDetails.getAdresse());
        }
    }
}
