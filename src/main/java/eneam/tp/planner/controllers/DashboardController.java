package eneam.tp.planner.controllers;

import eneam.tp.planner.models.Conducteur;
import eneam.tp.planner.models.Mission;
import eneam.tp.planner.models.Vehicule;
import eneam.tp.planner.services.ConducteurService;
import eneam.tp.planner.services.MissionService;
import eneam.tp.planner.services.VehiculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/web")
public class DashboardController {

    private final ConducteurService conducteurService;
    private final VehiculeService vehiculeService;
    private final MissionService missionService;

    @Autowired
    public DashboardController(
            ConducteurService conducteurService,
            VehiculeService vehiculeService,
            MissionService missionService) {
        this.conducteurService = conducteurService;
        this.vehiculeService = vehiculeService;
        this.missionService = missionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Récupérer les informations du tableau de bord
        List<Conducteur> conducteurs = conducteurService.getAllConducteurs();
        List<Vehicule> vehicules = vehiculeService.getAllVehicules();
        List<Mission> missions = missionService.getAllMissions();
        
        // Calculer quelques statistiques pour le tableau de bord
        long missionsPlanifiees = missions.stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.PLANIFIEE)
                .count();
        
        long missionsEnCours = missions.stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.EN_COURS)
                .count();
        
        long missionsTerminees = missions.stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.TERMINEE)
                .count();
        
        long missionsAnnulees = missions.stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.ANNULEE)
                .count();
        
        // Liste des missions à venir dans les 7 prochains jours
        List<Mission> prochainesMissions = missionService.getMissionsForPeriod(
                LocalDateTime.now(), 
                LocalDateTime.now().plusDays(7));
        
        // Ajouter les données au modèle
        model.addAttribute("totalConducteurs", conducteurs.size());
        model.addAttribute("totalVehicules", vehicules.size());
        model.addAttribute("totalMissions", missions.size());
        model.addAttribute("missionsPlanifiees", missionsPlanifiees);
        model.addAttribute("missionsEnCours", missionsEnCours);
        model.addAttribute("missionsTerminees", missionsTerminees);
        model.addAttribute("missionsAnnulees", missionsAnnulees);
        model.addAttribute("prochainesMissions", prochainesMissions);
        model.addAttribute("username", authentication.getName());
        
        return "dashboard/home";
    }
}