package eneam.tp.planner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "conducteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "missions"})
public class Conducteur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    private String telephone;
    
    @Column(unique = true, nullable = false)
    private String matricule;
    
    @OneToMany(mappedBy = "conducteur", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference(value = "conducteur-missions")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Mission> missions = new ArrayList<>();
    
    // Helper methods
    public void addMission(Mission mission) {
        missions.add(mission);
        mission.setConducteur(this);
    }
    
    public void removeMission(Mission mission) {
        missions.remove(mission);
        mission.setConducteur(null);
    }
}