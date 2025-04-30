package eneam.tp.planner.controllers;

import eneam.tp.planner.dto.ConducteurMissionDTO;
import eneam.tp.planner.exceptions.ConflictException;
import eneam.tp.planner.exceptions.MissionChevauchementException;
import eneam.tp.planner.exceptions.ResourceNotFoundException;
import eneam.tp.planner.exceptions.VehiculeImmatriculationExistsException;
import eneam.tp.planner.models.*;
import eneam.tp.planner.services.*;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/web")
public class WebControllers {

    private final ConducteurService conducteurService;
    private final VehiculeService vehiculeService;
    private final MissionService missionService;

    @Autowired
    public WebControllers(
            ConducteurService conducteurService,
            VehiculeService vehiculeService,
            MissionService missionService) {
        this.conducteurService = conducteurService;
        this.vehiculeService = vehiculeService;
        this.missionService = missionService;
    }

    @GetMapping("/conducteurs")
    public String listConducteurs(Model model) {
        List<Conducteur> conducteurs = conducteurService.getAllConducteursWithMissions();

        // Create a map of conducteur availability
        Map<Long, Boolean> conducteurDisponibilite = conducteurs.stream()
                .collect(Collectors.toMap(
                        Conducteur::getId,
                        conducteur -> conducteurService.isDisponible(conducteur)));

        model.addAttribute("conducteurs", conducteurs);
        model.addAttribute("conducteurDisponibilite", conducteurDisponibilite);

        int totalConducteurs = conducteurs.size();
        model.addAttribute("totalConducteurs", totalConducteurs);

        int totalConducteursEnMission = (int) conducteurs.stream()
                .filter(c -> c.getMissions().stream()
                        .anyMatch(m -> m.getStatut() != Mission.StatutMission.ANNULEE))
                .count();
        model.addAttribute("totalConducteursEnMission", totalConducteursEnMission);

        int totalConducteursSansMission = totalConducteurs - totalConducteursEnMission;
        model.addAttribute("totalConducteursSansMission", totalConducteursSansMission);

        return "conducteurs/list";
    }

    @GetMapping("/conducteurs/search")
    public String searchConducteurs(@RequestParam(required = false) String query, Model model) {
        List<Conducteur> conducteurs;

        if (StringUtils.isBlank(query)) {
            conducteurs = conducteurService.getAllConducteurs();
        } else {
            conducteurs = conducteurService.searchConducteurs(query);
            model.addAttribute("query", query);
        }
        // Create a map of conducteur availability
        Map<Long, Boolean> conducteurDisponibilite = conducteurs.stream()
                .collect(Collectors.toMap(
                        Conducteur::getId,
                        conducteur -> conducteurService.isDisponible(conducteur)));

        model.addAttribute("conducteurs", conducteurs);
        model.addAttribute("conducteurDisponibilite", conducteurDisponibilite);

        int totalConducteurs = conducteurs.size();
        model.addAttribute("totalConducteurs", totalConducteurs);

        int totalConducteursEnMission = (int) conducteurs.stream()
                .filter(c -> c.getMissions().stream()
                        .anyMatch(m -> m.getStatut() != Mission.StatutMission.ANNULEE))
                .count();
        model.addAttribute("totalConducteursEnMission", totalConducteursEnMission);

        int totalConducteursSansMission = totalConducteurs - totalConducteursEnMission;
        model.addAttribute("totalConducteursSansMission", totalConducteursSansMission);

        return "conducteurs/list"; // Your conducteur list template
    }

    @GetMapping("/conducteurs/new")
    public String newConducteurForm(Model model) {
        model.addAttribute("conducteur", new Conducteur());
        return "conducteurs/form";
    }

    @PostMapping("/conducteurs/save")
    public String saveConducteur(@ModelAttribute Conducteur conducteur, RedirectAttributes redirectAttributes) {
        conducteurService.saveConducteur(conducteur);
        redirectAttributes.addFlashAttribute("successMessage", "Conducteur créé avec succès !");
        return "redirect:/web/conducteurs";
    }

    @PostMapping("/conducteurs/{id}/update")
    public String updateConducteur(@PathVariable Long id, @ModelAttribute Conducteur conducteur,
            RedirectAttributes redirectAttributes) {
        conducteurService.updateConducteur(id, conducteur);
        redirectAttributes.addFlashAttribute("successMessage", "Conducteur mis à jour avec succès !");
        return "redirect:/web/conducteurs";
    }

    @GetMapping("/conducteurs/{id}")
    public String showConducteur(@PathVariable Long id, Model model) {
        Conducteur conducteur = conducteurService.getConducteurById(id);
        model.addAttribute("conducteur", conducteur);

        int totalMissions = conducteur.getMissions().size();
        model.addAttribute("totalMissions", totalMissions);

        int missionsEnCours = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.EN_COURS).count();
        model.addAttribute("missionsEnCours", missionsEnCours);

        int missionsTerminees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.TERMINEE).count();
        model.addAttribute("missionsTerminees", missionsTerminees);

        int missionsAnnulees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.ANNULEE).count();
        model.addAttribute("missionsAnnulees", missionsAnnulees);

        int missionsPlanifiees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.PLANIFIEE).count();
        model.addAttribute("missionsPlanifiees", missionsPlanifiees);
        return "conducteurs/detail";
    }

    @GetMapping("/conducteurs/{id}/edit")
    public String editConducteur(@PathVariable Long id, Model model) {
        Conducteur conducteur = conducteurService.getConducteurById(id);
        model.addAttribute("conducteur", conducteur);

        int totalMissions = conducteur.getMissions().size();
        model.addAttribute("totalMissions", totalMissions);

        int missionsEnCours = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.EN_COURS).count();
        model.addAttribute("missionsEnCours", missionsEnCours);

        int missionsTerminees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.TERMINEE).count();
        model.addAttribute("missionsTerminees", missionsTerminees);

        int missionsAnnulees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.ANNULEE).count();
        model.addAttribute("missionsAnnulees", missionsAnnulees);

        int missionsPlanifiees = (int) conducteur.getMissions().stream()
                .filter(m -> m.getStatut() == Mission.StatutMission.PLANIFIEE).count();
        model.addAttribute("missionsPlanifiees", missionsPlanifiees);
        return "conducteurs/form";
    }

    @DeleteMapping("/conducteurs/{id}/delete")
    public String deleteConducteur(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            conducteurService.deleteConducteur(id);
            redirectAttributes.addFlashAttribute("successMessage", "Conducteur supprimé avec succès");
            return "redirect:/web/conducteurs";
        } catch (ConflictException c) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Impossible de supprimer le conducteur car il a des missions en cours ou planifiées");
            return "redirect:/web/conducteurs";
        } catch (ResourceNotFoundException r) {
            redirectAttributes.addFlashAttribute("errorMessage", "Conducteur introuvable");
            return "redirect:/web/conducteurs";
        } catch (Exception e) {
            System.out.println("Delete conducteur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer le conducteur");
            return "redirect:/web/conducteurs";
        }
    }

    @GetMapping("/vehicules")
    public String listVehicules(Model model, HttpServletRequest request) {
        String type = request.getParameter("type");
        if (type != null) {
            model.addAttribute("type", type);
        }
        List<Vehicule> vehicules = vehiculeService.getAllVehiculesWithMissions();
        if (type != null) {
            vehicules = vehicules.stream()
                    .filter(v -> v.getType().name().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        model.addAttribute("vehicules", vehicules);

        Map<Long, Boolean> vehiculeDisponibilite = vehicules.stream()
                .collect(Collectors.toMap(
                        Vehicule::getId,
                        vehicule -> vehiculeService.isDisponible(vehicule)));
        model.addAttribute("vehiculeDisponibilite", vehiculeDisponibilite);
        return "vehicules/list";
    }

    @GetMapping("/vehicules/search")
    public String searchVehicules(@RequestParam(required = false) String query, Model model) {
        List<Vehicule> vehicules;

        if (StringUtils.isBlank(query)) {
            vehicules = vehiculeService.getAllVehicules();
        } else {
            vehicules = vehiculeService.searchVehicules(query);
            model.addAttribute("query", query);
        }
        // Create a map of vehicule availability
        Map<Long, Boolean> vehiculeDisponibilite = vehicules.stream()
                .collect(Collectors.toMap(
                        Vehicule::getId,
                        vehicule -> vehiculeService.isDisponible(vehicule)));

        model.addAttribute("vehicules", vehicules);
        model.addAttribute("vehiculeDisponibilite", vehiculeDisponibilite);
        return "vehicules/list"; // Your conducteur list template
    }

    @GetMapping("/vehicules/new")
    public String newVehiculeForm(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("types", Vehicule.TypeVehicule.values());
        return "vehicules/form";
    }

    @GetMapping("/vehicules/{id}/edit")
    public String editVehicule(@PathVariable Long id, Model model) {
        Vehicule vehicule = vehiculeService.getVehiculeById(id);
        model.addAttribute("vehicule", vehicule);
        model.addAttribute("types", Vehicule.TypeVehicule.values());
        return "vehicules/form";
    }

    @PostMapping("/vehicules/{id}/update")
    public String updateVehicule(@PathVariable Long id, @ModelAttribute Vehicule vehicule,
            RedirectAttributes redirectAttributes) {
        try {
            vehiculeService.updateVehicule(id, vehicule);
            redirectAttributes.addFlashAttribute("successMessage", "Véhicule mis à jour avec succès !");
        } catch (VehiculeImmatriculationExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/vehicules/" + id + "/edit";
        }
        return "redirect:/web/vehicules";
    }

    @GetMapping("/vehicules/{id}/detail")
    public String showVehicule(@PathVariable Long id, Model model) {
        Vehicule vehicule = vehiculeService.getVehiculeById(id);
        model.addAttribute("vehicule", vehicule);
        return "vehicules/detail";
    }

    @DeleteMapping("/vehicules/{id}/delete")
    public String deleteVehicule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehiculeService.deleteVehicule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Véhicule supprimé avec succès");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Véhicule introuvable");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer le véhicule");
        }
        return "redirect:/web/vehicules";
    }

    @PostMapping("/vehicules/save")
    public String saveVehicule(@ModelAttribute Vehicule vehicule, RedirectAttributes redirectAttributes, Model model) {
        try {
            vehiculeService.saveVehicule(vehicule);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Le véhicule a été enregistré avec succès");
            return "redirect:/web/vehicules";
        } catch (VehiculeImmatriculationExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("vehicule", vehicule);
            return "vehicules/form"; // Return to the form page
        }
    }

    @GetMapping("/missions")
    public String listMissions(
            @RequestParam(required = false) Long conducteurId,
            @RequestParam(required = false) Long vehiculeId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String statut,
            Model model) {

        List<Mission> missions;

        // Parse dates first
        final LocalDateTime debutDate, finDate;
        {
            LocalDateTime tempDebut = null;
            LocalDateTime tempFin = null;

            if (dateDebut != null && !dateDebut.trim().isEmpty() &&
                    dateFin != null && !dateFin.trim().isEmpty()) {
                try {
                    // Decode URL-encoded strings
                    String decodedDateDebut = dateDebut.replace("+", " ");
                    String decodedDateFin = dateFin.replace("+", " ");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    tempDebut = LocalDateTime.parse(decodedDateDebut, formatter);
                    tempFin = LocalDateTime.parse(decodedDateFin, formatter);

                } catch (DateTimeParseException e) {
                    try {
                        // Try alternative format
                        DateTimeFormatter alternativeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        tempDebut = LocalDateTime.parse(dateDebut, alternativeFormatter);
                        tempFin = LocalDateTime.parse(dateFin, alternativeFormatter);
                    } catch (DateTimeParseException ex) {
                        model.addAttribute("errorMessage",
                                "Format de date invalide. Format attendu: YYYY-MM-DD HH:mm");
                    }
                }
            }
            debutDate = tempDebut;
            finDate = tempFin;
        }

        try {
            // Start with all missions
            missions = missionService.getAllMissions();

            // Apply filters
            if (conducteurId != null) {
                missions = missions.stream()
                        .filter(m -> m.getConducteur() != null &&
                                m.getConducteur().getId().equals(conducteurId))
                        .collect(Collectors.toList());
            }

            if (vehiculeId != null) {
                missions = missions.stream()
                        .filter(m -> m.getVehicule() != null &&
                                m.getVehicule().getId().equals(vehiculeId))
                        .collect(Collectors.toList());
            }

            if (debutDate != null && finDate != null) {
                missions = missions.stream()
                        .filter(m -> !m.getDateDebut().isBefore(debutDate) &&
                                !m.getDateDebut().isAfter(finDate))
                        .collect(Collectors.toList());
            }

            if (statut != null && !statut.trim().isEmpty()) {
                missions = missions.stream()
                        .filter(m -> m.getStatut().name().equals(statut))
                        .collect(Collectors.toList());
            }

            // Calculate statistics
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

            // Get lists for dropdowns
            List<Conducteur> conducteurs = conducteurService.getAllConducteurs();
            List<Vehicule> vehicules = vehiculeService.getAllVehicules();

            // Add everything to model
            model.addAttribute("missions", missions);
            model.addAttribute("conducteurs", conducteurs);
            model.addAttribute("vehicules", vehicules);
            model.addAttribute("missionsPlanifiees", missionsPlanifiees);
            model.addAttribute("missionsEnCours", missionsEnCours);
            model.addAttribute("missionsTerminees", missionsTerminees);
            model.addAttribute("missionsAnnulees", missionsAnnulees);

            // Add filters to model
            model.addAttribute("filtreStatut", statut);
            model.addAttribute("filtreConducteurId", conducteurId);
            model.addAttribute("filtreVehiculeId", vehiculeId);
            model.addAttribute("filtreDateDebut", dateDebut);
            model.addAttribute("filtreDateFin", dateFin);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors du filtrage des missions");
            missions = missionService.getAllMissions();
        }

        return "missions/list";
    }

    @GetMapping("/missions/{id}/edit")
    public String editMission(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            String error = request.getParameter("error");
            if (error != null) {
                model.addAttribute("errorMessage", URLDecoder.decode(error, StandardCharsets.UTF_8));
            }

            Mission mission = missionService.getMissionById(id);
            model.addAttribute("mission", mission);
            model.addAttribute("conducteurs", conducteurService.getAllConducteurs());
            model.addAttribute("vehicules", vehiculeService.getAllVehicules());
            model.addAttribute("statuts", Mission.StatutMission.values());
            return "missions/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la récupération de la mission");
            return "redirect:/web/missions"; // Redirect to the list page in case of error
        }
    }

    @GetMapping("/missions/new")
    public String newMissionForm(Model model, HttpServletRequest request) {
        String error = request.getParameter("error");
        if (error != null) {
            model.addAttribute("errorMessage", URLDecoder.decode(error, StandardCharsets.UTF_8));
        }
        if (!model.containsAttribute("mission")) {
            model.addAttribute("mission", new Mission());
        }
        model.addAttribute("conducteurs", conducteurService.getAllConducteurs());
        model.addAttribute("vehicules", vehiculeService.getAllVehicules());
        model.addAttribute("statuts", Mission.StatutMission.values());
        return "missions/form";
    }

    @PostMapping("/missions/save")
    public String saveMission(@ModelAttribute Mission mission, RedirectAttributes redirectAttributes) {
        try {
            if (mission.getId() == null) {
                missionService.createMission(mission);
                redirectAttributes.addFlashAttribute("successMessage", "Mission créée avec succès !");
            } else {
                missionService.updateMission(mission.getId(), mission);
                redirectAttributes.addFlashAttribute("successMessage", "Mission mise à jour avec succès !");
            }
            return "redirect:/web/missions";
        } catch (MissionChevauchementException exception) {
            redirectAttributes.addFlashAttribute("mission", mission);
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/web/missions/new";
        }
    }

    @GetMapping("/missions/{id}")
    public String showMission(@PathVariable Long id, Model model) {
        Mission mission = missionService.getMissionById(id);
        model.addAttribute("mission", mission);
        return "missions/detail";
    }

    @GetMapping("/missions/{id}/annuler")
    public String annulerMission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            missionService.annulerMission(id);
            redirectAttributes.addFlashAttribute("successMessage", "Mission annulée avec succès !");
            return "redirect:/web/missions";
        } catch (ResourceNotFoundException e) {
            // Handle exception if needed
            redirectAttributes.addFlashAttribute("errorMessage", "Mission introuvable");
            return "redirect:/web/missions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'annulation de la mission");
            return "redirect:/web/missions";
        }
    }

    @GetMapping("/rapport")
    public String rapportForm(Model model) {
        model.addAttribute("debut", LocalDateTime.now());
        model.addAttribute("fin", LocalDateTime.now().plusDays(7));
        return "rapport/form";
    }

    @PostMapping("/rapport/generer")
    public String genererRapport(
            @RequestParam String debut,
            @RequestParam String fin,
            @RequestParam String statut,
            @RequestParam String format,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response) throws IOException,DocumentException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime debutDateTime = LocalDateTime.parse(debut, formatter);
        LocalDateTime finDateTime = LocalDateTime.parse(fin, formatter);

        if (debutDateTime.isAfter(finDateTime)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "La date de début doit être antérieure à la date de fin");
            return "redirect:/web/rapport";
        }

        // Utiliser debutDateTime et finDateTime
        model.addAttribute("debut", debutDateTime);
        model.addAttribute("fin", finDateTime);
        List<ConducteurMissionDTO> conducteursMissions = missionService.generateRapportConducteursPeriode(debutDateTime,
                finDateTime, statut);

        int totalMissions = conducteursMissions.stream()
                .mapToInt(c -> c.getMissions().size())
                .sum();

        model.addAttribute("totalMissions", totalMissions);
        model.addAttribute("conducteursMissions", conducteursMissions);
        model.addAttribute("statut", statut);

        if (format.equals("pdf")) {
            exportToPDF(response, conducteursMissions, debutDateTime, finDateTime, statut, totalMissions);
            return null;
        } else if (format.equals("excel")) {
            exportToExcel(response, conducteursMissions, debutDateTime, finDateTime, statut, totalMissions);
            return null;
        }

        return "rapport/view";
    }

    private void exportToPDF(HttpServletResponse response, List<ConducteurMissionDTO> conducteursMissions,
            LocalDateTime debut, LocalDateTime fin, String statut, int totalMissions) throws IOException,DocumentException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=rapport.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Add report title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Rapport des Conducteurs en Mission", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add report details
        document.add(new Paragraph("Période: " + debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                " - " + fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph("Statut: " + statut));
        document.add(new Paragraph("Total missions: " + totalMissions));
        document.add(Chunk.NEWLINE);

        // Add table of missions per driver
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Header row
        Stream.of("ID", "Nom", "Prénom", "Matricule", "Missions", "Total Missions")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });

        // Data rows
        for (ConducteurMissionDTO conducteur : conducteursMissions) {
            table.addCell(String.valueOf(conducteur.getId()));
            table.addCell(conducteur.getNom());
            table.addCell(conducteur.getPrenom());
            table.addCell(conducteur.getMatricule());

            // List of missions
            String missionsList = conducteur.getMissions().stream()
                    .map(mission -> mission.getDescription() + " (" +
                            mission.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                            " - " +
                            mission.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")")
                    .collect(Collectors.joining("\n\n"));
            table.addCell(missionsList);

            table.addCell(String.valueOf(conducteur.getMissions().size()));
        }

        document.add(table);
        document.close();
    }

    private void exportToExcel(HttpServletResponse response, List<ConducteurMissionDTO> conducteursMissions,
            LocalDateTime debut, LocalDateTime fin, String statut, int totalMissions) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=rapport.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rapport");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nom");
        headerRow.createCell(2).setCellValue("Prénom");
        headerRow.createCell(3).setCellValue("Matricule");
        headerRow.createCell(4).setCellValue("Missions");
        headerRow.createCell(5).setCellValue("Total Missions");

        // Data rows
        int rowNum = 1;
        for (ConducteurMissionDTO conducteur : conducteursMissions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(conducteur.getId());
            row.createCell(1).setCellValue(conducteur.getNom());
            row.createCell(2).setCellValue(conducteur.getPrenom());
            row.createCell(3).setCellValue(conducteur.getMatricule());

            // List of missions
            String missionsList = conducteur.getMissions().stream()
                    .map(mission -> mission.getDescription() + " (" +
                            mission.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                            " - " +
                            mission.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")")
                    .collect(Collectors.joining("\n"));
            row.createCell(4).setCellValue(missionsList);

            row.createCell(5).setCellValue(conducteur.getMissions().size());
        }

        // Auto-size columns
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write report details
        sheet.createRow(rowNum++);
        sheet.createRow(rowNum++).createCell(0)
                .setCellValue("Période: " + debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                        " - " + fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        sheet.createRow(rowNum++).createCell(0).setCellValue("Statut: " + statut);
        sheet.createRow(rowNum).createCell(0).setCellValue("Total missions: " + totalMissions);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}