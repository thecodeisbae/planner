package eneam.tp.planner.controllers;

import eneam.tp.planner.models.*;
import eneam.tp.planner.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {
    
    private final VehiculeService vehiculeService;
    private final MissionService missionService;
    
    @Autowired
    public VehiculeController(
            VehiculeService vehiculeService, 
            MissionService missionService) {
        this.vehiculeService = vehiculeService;
        this.missionService = missionService;
    }
    
    @GetMapping
    public ResponseEntity<List<Vehicule>> getAllVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAllVehicules();
        return new ResponseEntity<>(vehicules, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> getVehiculeById(@PathVariable Long id) {
        Vehicule vehicule = vehiculeService.getVehiculeById(id);
        return new ResponseEntity<>(vehicule, HttpStatus.OK);
    }
    
    @GetMapping("/immatriculation/{immatriculation}")
    public ResponseEntity<Vehicule> getVehiculeByImmatriculation(@PathVariable String immatriculation) {
        Vehicule vehicule = vehiculeService.getVehiculeByImmatriculation(immatriculation);
        return new ResponseEntity<>(vehicule, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Vehicule> createVehicule(@RequestBody Vehicule vehicule) {
        Vehicule newVehicule = vehiculeService.saveVehicule(vehicule);
        return new ResponseEntity<>(newVehicule, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> updateVehicule(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        Vehicule updatedVehicule = vehiculeService.updateVehicule(id, vehicule);
        return new ResponseEntity<>(updatedVehicule, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        vehiculeService.deleteVehicule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/{id}/missions")
    public ResponseEntity<List<Mission>> getMissionsByVehicule(@PathVariable Long id) {
        List<Mission> missions = missionService.getMissionsByVehicule(id);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }
}