package eneam.tp.planner.dto;

import eneam.tp.planner.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionSimpleDTO {
    private Long id;
    private String description;
    private String destination;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String observations;
    private Mission.StatutMission statut;
    private Long vehiculeId;
    private String vehiculeImmatriculation;

    public static MissionSimpleDTO fromEntity(Mission mission) {
        MissionSimpleDTO dto = new MissionSimpleDTO();
        dto.setId(mission.getId());
        dto.setDescription(mission.getDescription());
        dto.setDestination(mission.getDestination());
        dto.setDateDebut(mission.getDateDebut());
        dto.setDateFin(mission.getDateFin());
        dto.setObservations(mission.getObservations());
        dto.setStatut(mission.getStatut());
        return dto;
    }
}