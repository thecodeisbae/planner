// ConducteurRepository.java
package eneam.tp.planner.repositories;

import eneam.tp.planner.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {
    Optional<Conducteur> findByMatricule(String matricule);

    @Query("SELECT DISTINCT c FROM Conducteur c LEFT JOIN FETCH c.missions")
    List<Conducteur> findAllWithMissions();
    
    @Query("SELECT c FROM Conducteur c LEFT JOIN FETCH c.missions WHERE c.id = :id")
    Optional<Conducteur> findByIdWithMissions(@Param("id") Long id);
    
    @Query("SELECT c FROM Conducteur c WHERE " +
       "LOWER(c.matricule) LIKE LOWER(:searchTerm) OR " +
       "LOWER(c.nom) LIKE LOWER(:searchTerm) OR " +
       "LOWER(c.prenom) LIKE LOWER(:searchTerm) OR " +
       "LOWER(c.telephone) LIKE LOWER(:searchTerm)")
    List<Conducteur> searchConducteurs(@Param("searchTerm") String searchTerm);
}



