package eneam.tp.planner.controllers;

import eneam.tp.planner.dto.ConducteurDTO;
import eneam.tp.planner.dto.MissionDTO;
import eneam.tp.planner.models.*;
import eneam.tp.planner.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController {
    
    private final ConducteurService conducteurService;
    private final MissionService missionService;
    
    @Autowired
    public ConducteurController(
            ConducteurService conducteurService, 
            MissionService missionService) {
        this.conducteurService = conducteurService;
        this.missionService = missionService;
    }
    
    @GetMapping
    public ResponseEntity<List<ConducteurDTO>> getAllConducteurs() {
        List<Conducteur> conducteurs = conducteurService.getAllConducteurs();
        List<ConducteurDTO> conducteurDTOs = conducteurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(conducteurDTOs, HttpStatus.OK);
    }

    // Méthode pour convertir un Conducteur en ConducteurDTO
    private ConducteurDTO convertToDTO(Conducteur conducteur) {
        ConducteurDTO dto = new ConducteurDTO();
        dto.setId(conducteur.getId());
        dto.setNom(conducteur.getNom());
        dto.setPrenom(conducteur.getPrenom());
        dto.setMatricule(conducteur.getMatricule());
        dto.setTelephone(conducteur.getTelephone());
        
        // Ajouter les missions du conducteur dans le DTO
        List<Mission> missions = missionService.getMissionsByConducteur(conducteur.getId());
        List<MissionDTO> missionDTOs = missions.stream()
                .map(missionService::convertToDTO)
                .collect(Collectors.toList());
        dto.setMissions(missionDTOs);
        
        return dto;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Conducteur> getConducteurById(@PathVariable Long id) {
        Conducteur conducteur = conducteurService.getConducteurById(id);
        return new ResponseEntity<>(conducteur, HttpStatus.OK);
    }
    
    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<Conducteur> getConducteurByMatricule(@PathVariable String matricule) {
        Conducteur conducteur = conducteurService.getConducteurByMatricule(matricule);
        return new ResponseEntity<>(conducteur, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Conducteur> createConducteur(@RequestBody Conducteur conducteur) {
        Conducteur newConducteur = conducteurService.saveConducteur(conducteur);
        return new ResponseEntity<>(newConducteur, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Conducteur> updateConducteur(@PathVariable Long id, @RequestBody Conducteur conducteur) {
        Conducteur updatedConducteur = conducteurService.updateConducteur(id, conducteur);
        return new ResponseEntity<>(updatedConducteur, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConducteur(@PathVariable Long id) {
        conducteurService.deleteConducteur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/{id}/missions")
    public ResponseEntity<List<Mission>> getMissionsByConducteur(@PathVariable Long id) {
        List<Mission> missions = missionService.getMissionsByConducteur(id);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }
}