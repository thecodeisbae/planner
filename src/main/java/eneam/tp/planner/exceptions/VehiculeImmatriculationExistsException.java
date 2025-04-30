package eneam.tp.planner.exceptions;

public class VehiculeImmatriculationExistsException extends RuntimeException {
    public VehiculeImmatriculationExistsException(String immatriculation) {
        super("Un véhicule avec l'immatriculation '" + immatriculation + "' existe deja");
    }
}
