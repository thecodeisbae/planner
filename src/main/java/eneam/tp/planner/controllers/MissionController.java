package eneam.tp.planner.controllers;

import eneam.tp.planner.dto.*;
import eneam.tp.planner.models.*;
import eneam.tp.planner.services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions")
public class MissionController {
    
    private final MissionService missionService;
    
    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }
    
    @GetMapping
    public ResponseEntity<List<MissionDTO>> getAllMissions() {
        List<Mission> missions = missionService.getAllMissions();
        List<MissionDTO> missionDTOs = missions.stream()
                .map(missionService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(missionDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        MissionDTO missionDTO = missionService.convertToDTO(mission);
        return new ResponseEntity<>(missionDTO, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<MissionDTO> createMission(@RequestBody Mission mission) {
        Mission newMission = missionService.createMission(mission);
        MissionDTO missionDTO = missionService.convertToDTO(newMission);
        return new ResponseEntity<>(missionDTO, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MissionDTO> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        Mission updatedMission = missionService.updateMission(id, mission);
        MissionDTO missionDTO = missionService.convertToDTO(updatedMission);
        return new ResponseEntity<>(missionDTO, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/annuler")
    public ResponseEntity<MissionDTO> annulerMission(@PathVariable Long id) {
        Mission mission = missionService.annulerMission(id);
        MissionDTO missionDTO = missionService.convertToDTO(mission);
        return new ResponseEntity<>(missionDTO, HttpStatus.OK);
    }
    
    @GetMapping("/periode")
    public ResponseEntity<List<MissionDTO>> getMissionsForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Mission> missions = missionService.getMissionsForPeriod(debut, fin);
        List<MissionDTO> missionDTOs = missions.stream()
                .map(missionService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(missionDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/rapport/conducteurs")
    public ResponseEntity<List<ConducteurMissionDTO>> getRapportConducteurs(
            @RequestParam String debut,
            @RequestParam String fin,
            @RequestParam String statut) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime debutDateTime = LocalDateTime.parse(debut, formatter);
        LocalDateTime finDateTime = LocalDateTime.parse(fin, formatter);
        List<ConducteurMissionDTO> rapport = missionService.generateRapportConducteursPeriode(debutDateTime, finDateTime,statut);
        return new ResponseEntity<>(rapport, HttpStatus.OK);
    }
    
    @GetMapping("/conducteurs-en-mission")
    public ResponseEntity<List<Conducteur>> getConducteursEnMission(
            @RequestParam String debut,
            @RequestParam String fin,
            @RequestParam String statut) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime debutDateTime = LocalDateTime.parse(debut, formatter);
        LocalDateTime finDateTime = LocalDateTime.parse(fin, formatter);
        List<Conducteur> conducteurs = missionService.getConducteursEnMission(debutDateTime, finDateTime, statut);
        return new ResponseEntity<>(conducteurs, HttpStatus.OK);
    }
}