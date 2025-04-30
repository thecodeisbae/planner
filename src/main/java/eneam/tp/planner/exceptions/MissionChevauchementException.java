package eneam.tp.planner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissionChevauchementException extends RuntimeException {
    
    public MissionChevauchementException(String message) {
        super(message);
    }
    
    public MissionChevauchementException(String resourceType, String conducteur, String dates) {
        super(String.format("Impossible de planifier la mission : %s (%s) deja en mission pour la periode %s", 
                resourceType, conducteur, dates));
    }
}