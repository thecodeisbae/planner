// MissionRepository.java
package eneam.tp.planner.repositories;

import eneam.tp.planner.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

       // Trouver les missions d'un conducteur
       List<Mission> findByConducteurId(Long conducteurId);

       // Trouver les missions d'un véhicule
       List<Mission> findByVehiculeId(Long vehiculeId);

       // Trouver les missions actives d'un conducteur qui pourraient chevaucher
       @Query("SELECT m FROM Mission m WHERE m.conducteur.id = :conducteurId " +
                     "AND m.statut != 'ANNULEE' " +
                     "AND ((m.dateDebut BETWEEN :debut AND :fin) " +
                     "OR (m.dateFin BETWEEN :debut AND :fin) " +
                     "OR (:debut BETWEEN m.dateDebut AND m.dateFin))")
       List<Mission> findChevauchementMissions(
                     @Param("conducteurId") Long conducteurId,
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin);

       // Trouver les missions actives d'un véhicule qui pourraient chevaucher
       @Query("SELECT m FROM Mission m WHERE m.vehicule.id = :vehiculeId " +
                     "AND m.statut != 'ANNULEE' " +
                     "AND ((m.dateDebut BETWEEN :debut AND :fin) " +
                     "OR (m.dateFin BETWEEN :debut AND :fin) " +
                     "OR (:debut BETWEEN m.dateDebut AND m.dateFin))")
       List<Mission> findChevauchementVehicule(
                     @Param("vehiculeId") Long vehiculeId,
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin);

       // Trouver les conducteurs en mission pour une période donnée
       @Query("SELECT DISTINCT m.conducteur FROM Mission m " +
                     "WHERE m.statut = :statut " +
                     "AND ((m.dateDebut BETWEEN :debut AND :fin) " +
                     "OR (m.dateFin BETWEEN :debut AND :fin) " +
                     "OR (:debut BETWEEN m.dateDebut AND m.dateFin))")
       List<Conducteur> findConducteursEnMission(
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin,
                     @Param("statut") Mission.StatutMission statut);

       // Trouver les conducteurs en mission pour une période donnée
       @Query("SELECT DISTINCT m.conducteur FROM Mission m " +
                     "WHERE ((m.dateDebut BETWEEN :debut AND :fin) " +
                     "OR (m.dateFin BETWEEN :debut AND :fin) " +
                     "OR (:debut BETWEEN m.dateDebut AND m.dateFin))")
       List<Conducteur> findConducteursEnMissionAll(
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin);

       // Trouver toutes les missions pour une période donnée
       @Query("SELECT m FROM Mission m WHERE " +
                     "((m.dateDebut BETWEEN :debut AND :fin) " +
                     "OR (m.dateFin BETWEEN :debut AND :fin) " +
                     "OR (:debut BETWEEN m.dateDebut AND m.dateFin))")
       List<Mission> findMissionsForPeriod(
                     @Param("debut") LocalDateTime debut,
                     @Param("fin") LocalDateTime fin);

       @Modifying
       @Query("DELETE FROM Mission m WHERE m.conducteur.id = :conducteurId")
       void deleteAllByConducteurId(@Param("conducteurId") Long conducteurId);
}