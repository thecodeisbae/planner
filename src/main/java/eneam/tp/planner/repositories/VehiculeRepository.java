package eneam.tp.planner.repositories;

import eneam.tp.planner.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    Optional<Vehicule> findByImmatriculation(String immatriculation);

    @Query("SELECT DISTINCT v FROM Vehicule v LEFT JOIN FETCH v.missions")
    List<Vehicule> findAllWithMissions();

    @Query("SELECT v FROM Vehicule v WHERE " +
       "LOWER(v.immatriculation) LIKE LOWER(:searchTerm) OR " +
       "LOWER(v.marque) LIKE LOWER(:searchTerm) OR " +
       "LOWER(v.modele) LIKE LOWER(:searchTerm) OR " +
       "CAST(v.annee as string) LIKE LOWER(:searchTerm)")
    List<Vehicule> searchVehicules(String searchTerm);
}