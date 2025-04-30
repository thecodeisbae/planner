package eneam.tp.planner.controllers;

import eneam.tp.planner.dto.UserRegistrationDTO;
import eneam.tp.planner.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
                             BindingResult result, Model model) {
        
        // Vérifier si le nom d'utilisateur existe déjà
        if (userService.usernameExists(userDTO.getUsername())) {
            result.rejectValue("username", "error.user", "Ce nom d'utilisateur est déjà utilisé");
        }
        
        // Vérifier si l'email existe déjà
        if (userService.emailExists(userDTO.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
        }
        
        // Vérifier si les mots de passe correspondent
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas");
        }
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Enregistrer l'utilisateur
        userService.registerNewUser(userDTO);
        
        return "redirect:/login?registered";
    }
}