package eneam.tp.planner.services;

import eneam.tp.planner.exceptions.*;
import eneam.tp.planner.models.*;
import eneam.tp.planner.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeService {
    
    private final VehiculeRepository vehiculeRepository;
    
    @Autowired
    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }
    
    public List<Vehicule> getAllVehicules() {
        return vehiculeRepository.findAll();
    }
    
    public List<Vehicule> getAllVehiculesWithMissions() {
        return vehiculeRepository.findAllWithMissions();
    }
    
    public Vehicule getVehiculeById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));
    }
    
    public Vehicule getVehiculeByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "immatriculation", immatriculation));
    }
    
    public Vehicule saveVehicule(Vehicule vehicule) {
        try {
            return vehiculeRepository.save(vehicule);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("UK77HMGQA9JIISLWJO63BN093RF_INDEX_2") || 
                e.getMessage().contains("immatriculation")) {
                throw new VehiculeImmatriculationExistsException(vehicule.getImmatriculation());
            }
            throw e;
        }
    }
    
    public Vehicule updateVehicule(Long id, Vehicule vehiculeDetails) {
        Vehicule vehicule = getVehiculeById(id);
        
        vehicule.setImmatriculation(vehiculeDetails.getImmatriculation());
        vehicule.setMarque(vehiculeDetails.getMarque());
        vehicule.setModele(vehiculeDetails.getModele());
        vehicule.setAnnee(vehiculeDetails.getAnnee());
        vehicule.setType(vehiculeDetails.getType());
        
        return vehiculeRepository.save(vehicule);
    }
    
    public void deleteVehicule(Long id) {
        Vehicule vehicule = getVehiculeById(id);
        vehiculeRepository.delete(vehicule);
    }

    public boolean isDisponible(Vehicule vehicule) {
        return vehicule.getMissions().stream()
            .noneMatch(mission -> 
                mission.getStatut() == Mission.StatutMission.EN_COURS || 
                mission.getStatut() == Mission.StatutMission.PLANIFIEE
            );
    }

    public List<Vehicule> searchVehicules(String query) {
        String searchTerm = "%" + query.toLowerCase() + "%";
        return vehiculeRepository.searchVehicules(searchTerm);
    }
}