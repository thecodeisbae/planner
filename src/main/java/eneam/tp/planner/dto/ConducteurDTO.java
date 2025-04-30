package eneam.tp.planner.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ConducteurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String matricule;
    private List<MissionDTO> missions = new ArrayList<>();
}