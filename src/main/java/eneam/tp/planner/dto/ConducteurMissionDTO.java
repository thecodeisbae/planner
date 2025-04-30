package eneam.tp.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConducteurMissionDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private List<MissionDTO> missions;
}
