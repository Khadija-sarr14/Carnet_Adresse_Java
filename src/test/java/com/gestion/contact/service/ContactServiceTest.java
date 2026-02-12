package com.gestion.contact.service;

import com.gestion.contact.entity.Contact;
import com.gestion.contact.exception.ContactNotFoundException;
import com.gestion.contact.exception.DuplicateContactException;
import com.gestion.contact.exception.InvalidContactException;
import com.gestion.contact.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ContactServiceImpl
 * Utilise Mockito pour simuler le repository
 * 
 * @author Personne 2 - Couche Service
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Contact")
class ContactServiceTest {
    
    @Mock
    private ContactRepository contactRepository;
    
    @InjectMocks
    private ContactServiceImpl contactService;
    
    private Contact testContact;
    private Contact testContact2;
    
    @BeforeEach
    void setUp() {
        // Initialisation d'un contact de test
        testContact = new Contact();
        testContact.setId(1L);
        testContact.setNom("Diop");
        testContact.setPrenom("Mamadou");
        testContact.setEmail("mamadou.diop@example.com");
        testContact.setTelephone("+221 77 123 45 67");
        testContact.setAdresse("Dakar, Sénégal");
        
        // Deuxième contact pour les tests
        testContact2 = new Contact();
        testContact2.setId(2L);
        testContact2.setNom("Ndiaye");
        testContact2.setPrenom("Fatou");
        testContact2.setEmail("fatou.ndiaye@example.com");
        testContact2.setTelephone("+221 76 987 65 43");
    }
    
    // ============ Tests de createContact() ============
    
    @Test
    @DisplayName("Créer un contact - Succès")
    void testCreateContact_Success() {
        // Arrange
        when(contactRepository.existsByEmail(testContact.getEmail())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);
        
        // Act
        Contact createdContact = contactService.createContact(testContact);
        
        // Assert
        assertNotNull(createdContact);
        assertEquals("Diop", createdContact.getNom());
        assertEquals("mamadou.diop@example.com", createdContact.getEmail());
        
        // Vérifier que les méthodes ont été appelées
        verify(contactRepository, times(1)).existsByEmail(testContact.getEmail());
        verify(contactRepository, times(1)).save(testContact);
    }
    
    @Test
    @DisplayName("Créer un contact - Contact null")
    void testCreateContact_NullContact() {
        // Act & Assert
        InvalidContactException exception = assertThrows(
            InvalidContactException.class,
            () -> contactService.createContact(null)
        );
        
        assertEquals("Le contact ne peut pas être null", exception.getMessage());
        verify(contactRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Créer un contact - Email déjà existant")
    void testCreateContact_DuplicateEmail() {
        // Arrange
        when(contactRepository.existsByEmail(testContact.getEmail())).thenReturn(true);
        
        // Act & Assert
        DuplicateContactException exception = assertThrows(
            DuplicateContactException.class,
            () -> contactService.createContact(testContact)
        );
        
        assertTrue(exception.getMessage().contains(testContact.getEmail()));
        verify(contactRepository, times(1)).existsByEmail(testContact.getEmail());
        verify(contactRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Créer un contact - Nom manquant")
    void testCreateContact_MissingNom() {
        // Arrange
        testContact.setNom(null);
        
        // Act & Assert
        InvalidContactException exception = assertThrows(
            InvalidContactException.class,
            () -> contactService.createContact(testContact)
        );
        
        assertTrue(exception.getMessage().contains("nom"));
        verify(contactRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Créer un contact - Email invalide")
    void testCreateContact_InvalidEmail() {
        // Arrange
        testContact.setEmail("email-invalide");
        
        // Act & Assert
        InvalidContactException exception = assertThrows(
            InvalidContactException.class,
            () -> contactService.createContact(testContact)
        );
        
        assertTrue(exception.getMessage().contains("email"));
        verify(contactRepository, never()).save(any());
    }

    
    // ============ Tests de getAllContacts() ============
    
    @Test
    @DisplayName("Récupérer tous les contacts - Liste non vide")
    void testGetAllContacts_NonEmpty() {
        // Arrange
        List<Contact> contacts = Arrays.asList(testContact, testContact2);
        when(contactRepository.findAll()).thenReturn(contacts);
        
        // Act
        List<Contact> result = contactService.getAllContacts();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Récupérer tous les contacts - Liste vide")
    void testGetAllContacts_Empty() {
        // Arrange
        when(contactRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Contact> result = contactService.getAllContacts();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository, times(1)).findAll();
    }
    
    // ============ Tests de getContactById() ============
    
    @Test
    @DisplayName("Récupérer un contact par ID - Succès")
    void testGetContactById_Success() {
        // Arrange
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        
        // Act
        Contact found = contactService.getContactById(1L);
        
        // Assert
        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Diop", found.getNom());
        verify(contactRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Récupérer un contact par ID - Contact non trouvé")
    void testGetContactById_NotFound() {
        // Arrange
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ContactNotFoundException exception = assertThrows(
            ContactNotFoundException.class,
            () -> contactService.getContactById(99L)
        );
        
        assertTrue(exception.getMessage().contains("99"));
        verify(contactRepository, times(1)).findById(99L);
    }
    
    @Test
    @DisplayName("Récupérer un contact par ID - ID null")
    void testGetContactById_NullId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> contactService.getContactById(null)
        );
        
        assertEquals("L'ID ne peut pas être null", exception.getMessage());
        verify(contactRepository, never()).findById(any());
    }
    
    // ============ Tests de getContactByEmail() ============
    
    @Test
    @DisplayName("Récupérer un contact par email - Succès")
    void testGetContactByEmail_Success() {
        // Arrange
        when(contactRepository.findByEmail("mamadou.diop@example.com"))
            .thenReturn(Optional.of(testContact));
        
        // Act
        Optional<Contact> found = contactService.getContactByEmail("mamadou.diop@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("mamadou.diop@example.com", found.get().getEmail());
        verify(contactRepository, times(1)).findByEmail("mamadou.diop@example.com");
    }
    
    @Test
    @DisplayName("Récupérer un contact par email - Non trouvé")
    void testGetContactByEmail_NotFound() {
        // Arrange
        when(contactRepository.findByEmail("inexistant@example.com"))
            .thenReturn(Optional.empty());
        
        // Act
        Optional<Contact> found = contactService.getContactByEmail("inexistant@example.com");
        
        // Assert
        assertFalse(found.isPresent());
        verify(contactRepository, times(1)).findByEmail("inexistant@example.com");
    }
    
    @Test
    @DisplayName("Récupérer un contact par email - Email null")
    void testGetContactByEmail_NullEmail() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> contactService.getContactByEmail(null));
        verify(contactRepository, never()).findByEmail(any());
    }
    
    @Test
    @DisplayName("Récupérer un contact par email - Email vide")
    void testGetContactByEmail_EmptyEmail() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> contactService.getContactByEmail("   "));
        verify(contactRepository, never()).findByEmail(any());
    }
    
    // ============ Tests de searchContactsByNom() ============
    
    @Test
    @DisplayName("Rechercher des contacts par nom - Résultats trouvés")
    void testSearchContactsByNom_Found() {
        // Arrange
        List<Contact> contacts = Arrays.asList(testContact);
        when(contactRepository.findByNomContainingIgnoreCase("Diop"))
            .thenReturn(contacts);
        
        // Act
        List<Contact> result = contactService.searchContactsByNom("Diop");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Diop", result.get(0).getNom());
        verify(contactRepository, times(1)).findByNomContainingIgnoreCase("Diop");
    }
    
    @Test
    @DisplayName("Rechercher des contacts par nom - Aucun résultat")
    void testSearchContactsByNom_NotFound() {
        // Arrange
        when(contactRepository.findByNomContainingIgnoreCase("Inconnu"))
            .thenReturn(Collections.emptyList());
        
        // Act
        List<Contact> result = contactService.searchContactsByNom("Inconnu");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository, times(1)).findByNomContainingIgnoreCase("Inconnu");
    }
    
    @Test
    @DisplayName("Rechercher des contacts par nom - Nom null")
    void testSearchContactsByNom_NullNom() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> contactService.searchContactsByNom(null));
        verify(contactRepository, never()).findByNomContainingIgnoreCase(any());
    }
    
    // ============ Tests de searchContactsByPrenom() ============
    
    @Test
    @DisplayName("Rechercher des contacts par prénom - Succès")
    void testSearchContactsByPrenom_Success() {
        // Arrange
        List<Contact> contacts = Arrays.asList(testContact);
        when(contactRepository.findByPrenomContainingIgnoreCase("Mamadou"))
            .thenReturn(contacts);
        
        // Act
        List<Contact> result = contactService.searchContactsByPrenom("Mamadou");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findByPrenomContainingIgnoreCase("Mamadou");
    }
    
    // ============ Tests de updateContact() ============
    
    @Test
    @DisplayName("Mettre à jour un contact - Succès")
    void testUpdateContact_Success() {
        // Arrange
        Contact updatedDetails = new Contact();
        updatedDetails.setNom("Diop Updated");
        updatedDetails.setPrenom("Mamadou");
        updatedDetails.setEmail("mamadou.diop@example.com");
        
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);
        
        // Act
        Contact result = contactService.updateContact(1L, updatedDetails);
        
        // Assert
        assertNotNull(result);
        verify(contactRepository, times(1)).findById(1L);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }
    
    @Test
    @DisplayName("Mettre à jour un contact - Contact non trouvé")
    void testUpdateContact_NotFound() {
        // Arrange
        Contact updatedDetails = new Contact();
        updatedDetails.setNom("Nouveau Nom");
        updatedDetails.setPrenom("Nouveau Prénom");
        updatedDetails.setEmail("nouveau@example.com");
        
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ContactNotFoundException.class,
            () -> contactService.updateContact(99L, updatedDetails));
        
        verify(contactRepository, times(1)).findById(99L);
        verify(contactRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Mettre à jour un contact - Email déjà utilisé")
    void testUpdateContact_DuplicateEmail() {
        // Arrange
        Contact updatedDetails = new Contact();
        updatedDetails.setEmail("fatou.ndiaye@example.com"); // Email de testContact2
        
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        when(contactRepository.findByEmail("fatou.ndiaye@example.com"))
            .thenReturn(Optional.of(testContact2));
        
        // Act & Assert
        assertThrows(DuplicateContactException.class,
            () -> contactService.updateContact(1L, updatedDetails));
        
        verify(contactRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Mettre à jour un contact - ID null")
    void testUpdateContact_NullId() {
        // Arrange
        Contact updatedDetails = new Contact();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> contactService.updateContact(null, updatedDetails));
        
        verify(contactRepository, never()).findById(any());
    }
    
    // ============ Tests de deleteContact() ============
    
    @Test
    @DisplayName("Supprimer un contact - Succès")
    void testDeleteContact_Success() {
        // Arrange
        when(contactRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(1L);
        
        // Act
        contactService.deleteContact(1L);
        
        // Assert
        verify(contactRepository, times(1)).existsById(1L);
        verify(contactRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("Supprimer un contact - Contact non trouvé")
    void testDeleteContact_NotFound() {
        // Arrange
        when(contactRepository.existsById(99L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ContactNotFoundException.class,
            () -> contactService.deleteContact(99L));
        
        verify(contactRepository, times(1)).existsById(99L);
        verify(contactRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Supprimer un contact - ID null")
    void testDeleteContact_NullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> contactService.deleteContact(null));
        
        verify(contactRepository, never()).existsById(any());
        verify(contactRepository, never()).deleteById(any());
    }
    
    // ============ Tests de emailExists() ============
    
    @Test
    @DisplayName("Vérifier si email existe - Existe")
    void testEmailExists_True() {
        // Arrange
        when(contactRepository.existsByEmail("mamadou.diop@example.com")).thenReturn(true);
        
        // Act
        boolean exists = contactService.emailExists("mamadou.diop@example.com");
        
        // Assert
        assertTrue(exists);
        verify(contactRepository, times(1)).existsByEmail("mamadou.diop@example.com");
    }
    
    @Test
    @DisplayName("Vérifier si email existe - N'existe pas")
    void testEmailExists_False() {
        // Arrange
        when(contactRepository.existsByEmail("inexistant@example.com")).thenReturn(false);
        
        // Act
        boolean exists = contactService.emailExists("inexistant@example.com");
        
        // Assert
        assertFalse(exists);
        verify(contactRepository, times(1)).existsByEmail("inexistant@example.com");
    }
    
    @Test
    @DisplayName("Vérifier si email existe - Email null")
    void testEmailExists_NullEmail() {
        // Act
        boolean exists = contactService.emailExists(null);
        
        // Assert
        assertFalse(exists);
        verify(contactRepository, never()).existsByEmail(any());
    }
    
    // ============ Tests de countContacts() ============
    
    @Test
    @DisplayName("Compter les contacts")
    void testCountContacts() {
        // Arrange
        when(contactRepository.count()).thenReturn(5L);
        
        // Act
        long count = contactService.countContacts();
        
        // Assert
        assertEquals(5L, count);
        verify(contactRepository, times(1)).count();
    }
    
    // ============ Tests de deleteAllContacts() ============
    
    @Test
    @DisplayName("Supprimer tous les contacts")
    void testDeleteAllContacts() {
        // Arrange
        doNothing().when(contactRepository).deleteAll();
        
        // Act
        contactService.deleteAllContacts();
        
        // Assert
        verify(contactRepository, times(1)).deleteAll();
    }
}
