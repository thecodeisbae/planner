package eneam.tp.planner.services;

import eneam.tp.planner.dto.*;
import eneam.tp.planner.models.*;
import eneam.tp.planner.repositories.*;
import eneam.tp.planner.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionService {
    
    private final MissionRepository missionRepository;
    private final ConducteurService conducteurService;
    private final VehiculeService vehiculeService;
    
    @Autowired
    public MissionService(
            MissionRepository missionRepository,
            ConducteurService conducteurService,
            VehiculeService vehiculeService) {
        this.missionRepository = missionRepository;
        this.conducteurService = conducteurService;
        this.vehiculeService = vehiculeService;
    }
    
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }
    
    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", id));
    }
    
    // Convertir une mission en DTO
    public MissionDTO convertToDTO(Mission mission) {
        MissionDTO dto = new MissionDTO();
        dto.setId(mission.getId());
        dto.setDescription(mission.getDescription());
        dto.setDestination(mission.getDestination());
        dto.setDateDebut(mission.getDateDebut());
        dto.setDateFin(mission.getDateFin());
        dto.setObservations(mission.getObservations());
        dto.setStatut(mission.getStatut());
        
        if (mission.getConducteur() != null) {
            dto.setConducteurId(mission.getConducteur().getId());
            dto.setConducteurNom(mission.getConducteur().getNom());
            dto.setConducteurPrenom(mission.getConducteur().getPrenom());
        }
        
        if (mission.getVehicule() != null) {
            dto.setVehiculeId(mission.getVehicule().getId());
            dto.setVehiculeImmatriculation(mission.getVehicule().getImmatriculation());
        }
        
        return dto;
    }
    
    // Créer une nouvelle mission avec vérification des chevauchements
    public Mission createMission(Mission mission) {
        // Récupérer le conducteur et le véhicule
        Conducteur conducteur = conducteurService.getConducteurById(mission.getConducteur().getId());
        Vehicule vehicule = vehiculeService.getVehiculeById(mission.getVehicule().getId());
        
        // Vérifier les chevauchements pour le conducteur
        List<Mission> chevauchementsConducteur = missionRepository.findChevauchementMissions(
                conducteur.getId(), mission.getDateDebut(), mission.getDateFin());
        
        if (!chevauchementsConducteur.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String periode = mission.getDateDebut().format(formatter) + " - " + mission.getDateFin().format(formatter);
            throw new MissionChevauchementException("Conducteur", conducteur.getNom()+' '+conducteur.getPrenom() , periode);
        }
        
        // Vérifier les chevauchements pour le véhicule
        List<Mission> chavauchementVehicule = missionRepository.findChevauchementVehicule(
                vehicule.getId(), mission.getDateDebut(), mission.getDateFin());
        
        if (!chavauchementVehicule.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String periode = mission.getDateDebut().format(formatter) + " - " + mission.getDateFin().format(formatter);
            throw new MissionChevauchementException("Véhicule", vehicule.getImmatriculation(), periode);
        }
        
        // Assigner le conducteur et le véhicule
        mission.setConducteur(conducteur);
        mission.setVehicule(vehicule);
        
        // Sauvegarder la mission
        return missionRepository.save(mission);
    }
    
    // Mettre à jour une mission existante
    public Mission updateMission(Long id, Mission missionDetails) {
        Mission mission = getMissionById(id);
        
        // Si le conducteur ou les dates changent, vérifier les chevauchements
        if (!mission.getConducteur().getId().equals(missionDetails.getConducteur().getId()) || 
            !mission.getDateDebut().equals(missionDetails.getDateDebut()) || 
            !mission.getDateFin().equals(missionDetails.getDateFin())) {
            
            // Vérifier les chevauchements pour le nouveau conducteur
            List<Mission> chevauchementsConducteur = missionRepository.findChevauchementMissions(
                    missionDetails.getConducteur().getId(), 
                    missionDetails.getDateDebut(), 
                    missionDetails.getDateFin());
            
            // Filtrer la mission actuelle
            chevauchementsConducteur = chevauchementsConducteur.stream()
                    .filter(m -> !m.getId().equals(id))
                    .collect(Collectors.toList());
            
            if (!chevauchementsConducteur.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String periode = missionDetails.getDateDebut().format(formatter) + " - " + 
                                  missionDetails.getDateFin().format(formatter);
                throw new MissionChevauchementException("Conducteur", missionDetails.getConducteur().getNom()+' '+missionDetails.getConducteur().getPrenom(), periode);
            }
        }
        
        // Si le véhicule ou les dates changent, vérifier les chevauchements
        if (!mission.getVehicule().getId().equals(missionDetails.getVehicule().getId()) || 
            !mission.getDateDebut().equals(missionDetails.getDateDebut()) || 
            !mission.getDateFin().equals(missionDetails.getDateFin())) {
            
            // Vérifier les chevauchements pour le nouveau véhicule
            List<Mission> chavauchementVehicule = missionRepository.findChevauchementVehicule(
                    missionDetails.getVehicule().getId(), 
                    missionDetails.getDateDebut(), 
                    missionDetails.getDateFin());
            
            // Filtrer la mission actuelle
            chavauchementVehicule = chavauchementVehicule.stream()
                    .filter(m -> !m.getId().equals(id))
                    .collect(Collectors.toList());
            
            if (!chavauchementVehicule.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String periode = missionDetails.getDateDebut().format(formatter) + " - " + 
                                  missionDetails.getDateFin().format(formatter);
                throw new MissionChevauchementException("Véhicule", missionDetails.getVehicule().getImmatriculation(), periode);
            }
        }
        
        // Récupérer le conducteur et le véhicule
        Conducteur conducteur = conducteurService.getConducteurById(missionDetails.getConducteur().getId());
        Vehicule vehicule = vehiculeService.getVehiculeById(missionDetails.getVehicule().getId());
        
        // Mettre à jour la mission
        mission.setDescription(missionDetails.getDescription());
        mission.setDestination(missionDetails.getDestination());
        mission.setDateDebut(missionDetails.getDateDebut());
        mission.setDateFin(missionDetails.getDateFin());
        mission.setObservations(missionDetails.getObservations());
        mission.setStatut(missionDetails.getStatut());
        mission.setConducteur(conducteur);
        mission.setVehicule(vehicule);
        
        return missionRepository.save(mission);
    }
    
    // Annuler une mission
    public Mission annulerMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatut(Mission.StatutMission.ANNULEE);
        return missionRepository.save(mission);
    }
    
    // Trouver les missions d'un conducteur
    public List<Mission> getMissionsByConducteur(Long conducteurId) {
        return missionRepository.findByConducteurId(conducteurId);
    }
    
    // Trouver les missions d'un véhicule
    public List<Mission> getMissionsByVehicule(Long vehiculeId) {
        return missionRepository.findByVehiculeId(vehiculeId);
    }
    
    // Trouver les conducteurs en mission pour une période donnée selon le type de mission
    public List<Conducteur> getConducteursEnMission(LocalDateTime debut, LocalDateTime fin, String statut) {
        Mission.StatutMission statutMission = null;
        switch (statut) {
            case "EN_COURS":
                statutMission = Mission.StatutMission.EN_COURS;
                break;
            case "PLANIFIEE":
                statutMission = Mission.StatutMission.PLANIFIEE;
                break;
            case "ANNULEE":
                statutMission = Mission.StatutMission.ANNULEE;
                break;
        }
        return missionRepository.findConducteursEnMission(debut, fin, statutMission);
    }
    
    // Trouver les conducteurs en mission pour une période donnée
    public List<Conducteur> getConducteursEnMissionAll(LocalDateTime debut, LocalDateTime fin) {
        return missionRepository.findConducteursEnMissionAll(debut, fin);
    }
    
    // Trouver toutes les missions pour une période donnée
    public List<Mission> getMissionsForPeriod(LocalDateTime debut, LocalDateTime fin) {
        return missionRepository.findMissionsForPeriod(debut, fin);
    }
    
    // Générer un rapport des conducteurs avec leurs missions pour une période donnée
    public List<ConducteurMissionDTO> generateRapportConducteursPeriode(LocalDateTime debut, LocalDateTime fin, String statut) {
        // Obtenir les conducteurs en mission pendant la période
        List<Conducteur> conducteurs = (statut.equals( "TOUS")) ? getConducteursEnMissionAll(debut, fin) : getConducteursEnMission(debut, fin, statut);
        
        // Pour chaque conducteur, récupérer ses missions durant la période
        return conducteurs.stream().map(conducteur -> {
            List<Mission> missions = missionRepository.findByConducteurId(conducteur.getId())
                    .stream()
                    .filter(mission -> {
                        // Vérifier si la mission est dans la période
                        boolean isInPeriod = (mission.getDateDebut().isAfter(debut) || mission.getDateDebut().isEqual(debut)) &&
                                             (mission.getDateFin().isBefore(fin) || mission.getDateFin().isEqual(fin));
                        System.out.println("Mission " + mission.getId() + " in period? " + isInPeriod);
                        return isInPeriod;
                    })
                    .collect(Collectors.toList());
                    
            List<MissionDTO> missionDTOs = missions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
            return new ConducteurMissionDTO(
                conducteur.getId(),
                conducteur.getNom(),
                conducteur.getPrenom(),
                conducteur.getMatricule(),
                missionDTOs
            );
        }).collect(Collectors.toList());
    }
}