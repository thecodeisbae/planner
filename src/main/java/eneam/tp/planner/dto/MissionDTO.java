package eneam.tp.planner.dto;

import eneam.tp.planner.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionDTO {
    private Long id;
    private String description;
    private String destination;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String observations;
    private Mission.StatutMission statut;
    private Long conducteurId;
    private String conducteurNom;
    private String conducteurPrenom;
    private Long vehiculeId;
    private String vehiculeImmatriculation;
}