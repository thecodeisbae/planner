package eneam.tp.planner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "missions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateDebut;
    
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateFin;
    
    private String observations;
    
    @Enumerated(EnumType.STRING)
    private StatutMission statut = StatutMission.PLANIFIEE;
    
    @ManyToOne
    @JoinColumn(name = "conducteur_id", nullable = false)
    @JsonBackReference(value = "conducteur-missions")
    private Conducteur conducteur;
    
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    @JsonBackReference(value = "vehicule-missions")
    private Vehicule vehicule;
    
    // Enum pour les statuts de mission
    public enum StatutMission {
        PLANIFIEE,
        EN_COURS,
        TERMINEE,
        ANNULEE
    }
    
    // Méthode pour vérifier si cette mission chevauche une autre mission donnée
    public boolean chevauche(Mission autreMission) {
        return (dateDebut.isBefore(autreMission.getDateFin()) || dateDebut.isEqual(autreMission.getDateFin())) && 
               (dateFin.isAfter(autreMission.getDateDebut()) || dateFin.isEqual(autreMission.getDateDebut()));
    }
}