package eneam.tp.planner.config;

import eneam.tp.planner.models.*;
import eneam.tp.planner.repositories.RoleRepository;
import eneam.tp.planner.repositories.UserRepository;
import eneam.tp.planner.services.ConducteurService;
import eneam.tp.planner.services.MissionService;
import eneam.tp.planner.services.VehiculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            @Autowired ConducteurService conducteurService,
            @Autowired VehiculeService vehiculeService,
            @Autowired MissionService missionService,
            @Autowired UserRepository userRepository,
            @Autowired RoleRepository roleRepository,
            @Autowired PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Initialiser les rôles
            initRoles(roleRepository);
            
            // Initialiser les utilisateurs
            initUsers(userRepository, roleRepository, passwordEncoder);
            
            // Créer des conducteurs
            Conducteur conducteur1 = new Conducteur();
            conducteur1.setNom("Dupont");
            conducteur1.setPrenom("Jean");
            conducteur1.setMatricule("C001");
            conducteur1.setTelephone("0611223344");
            conducteurService.saveConducteur(conducteur1);

            Conducteur conducteur2 = new Conducteur();
            conducteur2.setNom("Martin");
            conducteur2.setPrenom("Sophie");
            conducteur2.setMatricule("C002");
            conducteur2.setTelephone("0622334455");
            conducteurService.saveConducteur(conducteur2);

            Conducteur conducteur3 = new Conducteur();
            conducteur3.setNom("Dubois");
            conducteur3.setPrenom("Pierre");
            conducteur3.setMatricule("C003");
            conducteur3.setTelephone("0633445566");
            conducteurService.saveConducteur(conducteur3);

            Conducteur conducteur4 = new Conducteur();
            conducteur4.setNom("Lefevre");
            conducteur4.setPrenom("Marie");
            conducteur4.setMatricule("C004");
            conducteur4.setTelephone("0644556677");
            conducteurService.saveConducteur(conducteur4);

            Conducteur conducteur5 = new Conducteur();
            conducteur5.setNom("Moreau");
            conducteur5.setPrenom("Luc");
            conducteur5.setMatricule("C005");
            conducteur5.setTelephone("0655667788");
            conducteurService.saveConducteur(conducteur5);

            // Créer des véhicules
            Vehicule vehicule1 = new Vehicule();
            vehicule1.setImmatriculation("AA-123-BB");
            vehicule1.setMarque("Renault");
            vehicule1.setModele("Clio");
            vehicule1.setAnnee(2020);
            vehicule1.setType(Vehicule.TypeVehicule.BERLINE);
            vehiculeService.saveVehicule(vehicule1);

            Vehicule vehicule2 = new Vehicule();
            vehicule2.setImmatriculation("CC-456-DD");
            vehicule2.setMarque("Peugeot");
            vehicule2.setModele("3008");
            vehicule2.setAnnee(2021);
            vehicule2.setType(Vehicule.TypeVehicule.SUV);
            vehiculeService.saveVehicule(vehicule2);

            Vehicule vehicule3 = new Vehicule();
            vehicule3.setImmatriculation("EE-789-FF");
            vehicule3.setMarque("Citroen");
            vehicule3.setModele("Berlingo");
            vehicule3.setAnnee(2019);
            vehicule3.setType(Vehicule.TypeVehicule.UTILITAIRE);
            vehiculeService.saveVehicule(vehicule3);

            Vehicule vehicule4 = new Vehicule();
            vehicule4.setImmatriculation("GG-012-HH");
            vehicule4.setMarque("Volkswagen");
            vehicule4.setModele("Golf");
            vehicule4.setAnnee(2022);
            vehicule4.setType(Vehicule.TypeVehicule.BERLINE);
            vehiculeService.saveVehicule(vehicule4);

            Vehicule vehicule5 = new Vehicule();
            vehicule5.setImmatriculation("II-345-JJ");
            vehicule5.setMarque("Ford");
            vehicule5.setModele("Transit");
            vehicule5.setAnnee(2020);
            vehicule5.setType(Vehicule.TypeVehicule.UTILITAIRE);
            vehiculeService.saveVehicule(vehicule5);

            // Créer des missions
            Mission mission1 = new Mission();
            mission1.setDescription("Transport de documents");
            mission1.setDestination("Paris");
            mission1.setDateDebut(LocalDateTime.now().plusDays(1));
            mission1.setDateFin(LocalDateTime.now().plusDays(1).plusHours(4));
            mission1.setObservations("Documents confidentiels");
            mission1.setStatut(Mission.StatutMission.PLANIFIEE);
            mission1.setConducteur(conducteur1);
            mission1.setVehicule(vehicule1);
            missionService.createMission(mission1);

            Mission mission2 = new Mission();
            mission2.setDescription("Transport de matériel");
            mission2.setDestination("Lyon");
            mission2.setDateDebut(LocalDateTime.now().plusDays(2));
            mission2.setDateFin(LocalDateTime.now().plusDays(2).plusHours(6));
            mission2.setObservations("Matériel fragile");
            mission2.setStatut(Mission.StatutMission.PLANIFIEE);
            mission2.setConducteur(conducteur2);
            mission2.setVehicule(vehicule2);
            missionService.createMission(mission2);

            Mission mission3 = new Mission();
            mission3.setDescription("Transport de personnel");
            mission3.setDestination("Marseille");
            mission3.setDateDebut(LocalDateTime.now().plusDays(3));
            mission3.setDateFin(LocalDateTime.now().plusDays(4));
            mission3.setObservations("Réunion importante");
            mission3.setStatut(Mission.StatutMission.PLANIFIEE);
            mission3.setConducteur(conducteur3);
            mission3.setVehicule(vehicule3);
            missionService.createMission(mission3);

            Mission mission4 = new Mission();
            mission4.setDescription("Livraison de colis");
            mission4.setDestination("Bordeaux");
            mission4.setDateDebut(LocalDateTime.now().plusDays(5));
            mission4.setDateFin(LocalDateTime.now().plusDays(5).plusHours(3));
            mission4.setObservations("Colis volumineux");
            mission4.setStatut(Mission.StatutMission.PLANIFIEE);  
            mission4.setConducteur(conducteur4);
            mission4.setVehicule(vehicule4);
            missionService.createMission(mission4);

            Mission mission5 = new Mission();
            mission5.setDescription("Transport de marchandises");
            mission5.setDestination("Toulouse");
            mission5.setDateDebut(LocalDateTime.now().plusDays(6));
            mission5.setDateFin(LocalDateTime.now().plusDays(7)); 
            mission5.setObservations("Chargement lourd");
            mission5.setStatut(Mission.StatutMission.PLANIFIEE);
            mission5.setConducteur(conducteur5); 
            mission5.setVehicule(vehicule5);
            missionService.createMission(mission5);
        };
    }
    
    private void initRoles(RoleRepository roleRepository) {
        // Créer les rôles par défaut s'ils n'existent pas
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }
        
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("USER"));
        }
        
        if (roleRepository.findByName("MANAGER").isEmpty()) {
            roleRepository.save(new Role("MANAGER"));
        }
    }
    
    private void initUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        // Créer un utilisateur admin par défaut s'il n'existe pas
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@missions.com");
            adminUser.setFullName("Administrateur Système");
            adminUser.setEnabled(true);
            
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role userRole = roleRepository.findByName("USER").orElseThrow();
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            adminUser.setRoles(roles);
            
            userRepository.save(adminUser);
        }
    }
}