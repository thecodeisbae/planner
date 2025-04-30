package eneam.tp.planner.services;

import eneam.tp.planner.exceptions.*;
import eneam.tp.planner.models.*;
import eneam.tp.planner.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConducteurService {
    
    private final ConducteurRepository conducteurRepository;

    // Use entityManager to ensure we have a managed entity
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    public ConducteurService(ConducteurRepository conducteurRepository) {
        this.conducteurRepository = conducteurRepository;
    }
    
    public List<Conducteur> getAllConducteurs() {
        return conducteurRepository.findAll();
    }
    
    public List<Conducteur> getAllConducteursWithMissions() {
        return conducteurRepository.findAllWithMissions();
    }

    public Conducteur getConducteurById(Long id) {
        return conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", id));
    }
    
    public Conducteur getConducteurByMatricule(String matricule) {
        return conducteurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "matricule", matricule));
    }
    
    public Conducteur saveConducteur(Conducteur conducteur) {
        return conducteurRepository.save(conducteur);
    }
    
    public Conducteur updateConducteur(Long id, Conducteur conducteurDetails) {
        Conducteur conducteur = getConducteurById(id);
        
        conducteur.setNom(conducteurDetails.getNom());
        conducteur.setPrenom(conducteurDetails.getPrenom());
        conducteur.setTelephone(conducteurDetails.getTelephone());
        conducteur.setMatricule(conducteurDetails.getMatricule());
        
        return conducteurRepository.save(conducteur);
    }

    public boolean isDisponible(Conducteur conducteur) {
        return conducteur.getMissions().stream()
            .noneMatch(mission -> 
                mission.getStatut() == Mission.StatutMission.EN_COURS || 
                mission.getStatut() == Mission.StatutMission.PLANIFIEE
            );
    }

    @Transactional
    public void deleteConducteur(Long id) {        
        // Get managed instance
        Conducteur conducteur = entityManager.find(Conducteur.class, id);
        if (conducteur == null) {
            throw new ResourceNotFoundException("Conducteur non trouvé");
        }
        
        if (hasMissions(conducteur)) {
            throw new ConflictException("Impossible de supprimer un conducteur avec des missions en cours");
        }

        // Get and delete all missions first
        String deleteMissionsQuery = "DELETE FROM Mission m WHERE m.conducteur.id = :conducteurId";
        entityManager.createQuery(deleteMissionsQuery)
            .setParameter("conducteurId", id)
            .executeUpdate();
        
        // Delete the conducteur
        String deleteConducteurQuery = "DELETE FROM Conducteur c WHERE c.id = :id";
        entityManager.createQuery(deleteConducteurQuery)
            .setParameter("id", id)
            .executeUpdate();
        
        // Flush to ensure all changes are written
        entityManager.flush();
    }

    private boolean hasMissions(Conducteur conducteur) {
        return conducteur.getMissions().stream()
            .anyMatch(mission -> 
                mission.getStatut() == Mission.StatutMission.EN_COURS || 
                mission.getStatut() == Mission.StatutMission.PLANIFIEE
            );
    }

    public List<Conducteur> searchConducteurs(String query) {
        String searchTerm = "%" + query.toLowerCase() + "%";
        return conducteurRepository.searchConducteurs(searchTerm);
    }
}