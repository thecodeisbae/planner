package eneam.tp.planner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "vehicules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "missions"})
public class Vehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String immatriculation;
    
    @Column(nullable = false)
    private String marque;
    
    @Column(nullable = false)
    private String modele;
    
    private int annee;
    
    @Enumerated(EnumType.STRING)
    private TypeVehicule type;
    
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "vehicule-missions")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Mission> missions = new ArrayList<>();
    
    // Enum pour les types de véhicules
    public enum TypeVehicule {
        BERLINE,
        SUV,
        UTILITAIRE,
        MINIBUS
    }
    
    // Helper methods
    public void addMission(Mission mission) {
        missions.add(mission);
        mission.setVehicule(this);
    }
    
    public void removeMission(Mission mission) {
        missions.remove(mission);
        mission.setVehicule(null);
    }
}