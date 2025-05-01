package eneam.tp.planner.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get the status code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "Une erreur s'est produite";
        String errorTitle = "Erreur";
        String errorCode = "";
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            errorCode = String.valueOf(statusCode);
            
            // Handle different error codes
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "Page Non Trouvée";
                errorMessage = "Désolé, la page que vous recherchez semble avoir été déplacée ou n'existe pas.";
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorTitle = "Accès Refusé";
                errorMessage = "Vous n'avez pas les droits d'accès nécessaires pour cette page.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorTitle = "Erreur Serveur";
                errorMessage = "Une erreur interne du serveur s'est produite. Veuillez réessayer plus tard.";
            }
        }
        
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorCode", errorCode);
        
        // Default error page for other errors
        return "error/error";
    }
}